package com.beepcast.subscriber.management;

import java.util.List;

import com.beepcast.subscriber.Module;
import com.beepcast.subscriber.SubscriberApp;
import com.beepcast.subscriber.SubscriberConf;
import com.beepcast.subscriber.SubscriberConf.CleanClientSubscriberUnsubsRecordConf;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class CleanClientSubscriberUnsubsRecord implements Module {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "CleanClientSubscriberUnsubsRecord" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean initialized;

  private SubscriberApp app;
  private SubscriberConf conf;
  private CleanClientSubscriberUnsubsRecordConf ccsxrConf;

  private CleanClientSubscriberDAO dao;

  private Thread workerThread;
  private boolean activeThread;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public CleanClientSubscriberUnsubsRecord( SubscriberApp app ,
      SubscriberConf conf ) {
    initialized = false;

    if ( app == null ) {
      return;
    }
    if ( conf == null ) {
      return;
    }

    this.app = app;
    this.conf = conf;
    this.ccsxrConf = conf.getCleanClientSubscriberUnsubsRecordConf();
    if ( ccsxrConf == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null clean client subscriber unsubs record conf" );
      return;
    }

    // setup dao
    dao = new CleanClientSubscriberDAO( conf.isDebug() );

    // setup worker thread
    workerThread = new CleanClientSubscriberUnsubsRecordThread();
    activeThread = false;

    // log it
    DLog.debug(
        lctx ,
        "Initialized : limitRecords = " + ccsxrConf.getLimitRecords()
            + " , sleepPerRecordInSeconds = "
            + ccsxrConf.getSleepPerRecordInSeconds()
            + " , sleepPerBatchInSeconds = "
            + ccsxrConf.getSleepPerBatchInSeconds()
            + " , sleepInitializedInSeconds = "
            + ccsxrConf.getSleepInitializedInSeconds() );

    initialized = true;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void moduleStart() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to start , found not yet initialized" );
      return;
    }
    activeThread = true;
    workerThread.start();
  }

  public void moduleStop() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to stop , found not yet initialized" );
      return;
    }
    activeThread = false;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // ////////////////////////////////////////////////////////////////////////////

  private class CleanClientSubscriberUnsubsRecordThread extends Thread {

    public CleanClientSubscriberUnsubsRecordThread() {
      super( "CleanClientSubscriberUnsubsRecordThread" );
    }

    public void run() {
      long counter = 0 , delay1s = 1000;
      DLog.debug( lctx , "Thread started" );
      while ( activeThread ) {

        // sleep every 1 second
        try {
          Thread.sleep( delay1s );
        } catch ( InterruptedException e ) {
        }
        counter = counter + delay1s;

        // load the duplicated list
        if ( counter % ( delay1s * ccsxrConf.getSleepInitializedInSeconds() ) == 0 ) {
          try {
            List listGrpItems = null;
            if ( ccsxrConf.getLimitRecords() > 0 ) {
              listGrpItems = dao
                  .listGroupDuplicatedActiveNumbersForUnsubs( ccsxrConf
                      .getLimitRecords() );
              DLog.debug( lctx , "Load group duplicated numbers : buffered = "
                  + ( listGrpItems == null ? -1 : listGrpItems.size() )
                  + " record(s)" );
              Thread.sleep( 100 );
            }
            List listSelItems = null;
            if ( ( listGrpItems != null ) && ( listGrpItems.size() > 0 ) ) {
              listSelItems = dao
                  .listDuplicatedActiveNumbersForUnsubs( listGrpItems );
              DLog.debug( lctx , "Load list duplicated numbers : buffered = "
                  + ( listSelItems == null ? -1 : listSelItems.size() )
                  + " record(s)" );
              Thread.sleep( 100 );
            }
            List listDelItems = null;
            if ( ( listSelItems != null ) && ( listSelItems.size() > 0 ) ) {
              listDelItems = dao.listDeletedNumbersForUnsubs( listSelItems );
              DLog.debug( lctx , "Load list deleted numbers : buffered = "
                  + ( listDelItems == null ? -1 : listDelItems.size() )
                  + " record(s)" );
              Thread.sleep( 100 );
            }
          } catch ( Exception e ) {
            DLog.warning( lctx , "Failed to clean the numbers , " + e );
          }
        }

        // the counter will reset every 24hours
        if ( counter < ( delay1s * 86400 ) ) {
          continue;
        }
        DLog.debug( lctx , "Reset loop counter : " + counter + " -> 0 time(s)" );
        counter = 0;

      } // while ( activeThread )
      DLog.debug( lctx , "Thread stopped" );
    } // public void run()
  }

}
