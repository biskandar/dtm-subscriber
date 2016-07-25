package com.beepcast.subscriber;

import com.beepcast.subscriber.management.CleanClientSubscriberDncRecord;
import com.beepcast.subscriber.management.CleanClientSubscriberInvalidRecord;
import com.beepcast.subscriber.management.CleanClientSubscriberUnsubsRecord;
import com.beepcast.subscriber.management.SynchSubscriberGroupReport;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberManagement implements Module {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberManagement" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean initialized;

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;

  private SynchSubscriberGroupReport synchSubscriberGroupReport;
  private CleanClientSubscriberUnsubsRecord cleanClientSubscriberUnsubsRecord;
  private CleanClientSubscriberInvalidRecord cleanClientSubscriberInvalidRecord;
  private CleanClientSubscriberDncRecord cleanClientSubscriberDncRecord;

  private Thread workerThread;
  private boolean activeThread;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberManagement( SubscriberApp subscriberApp ) {
    initialized = false;

    this.subscriberApp = subscriberApp;
    if ( subscriberApp == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null subscriber app" );
      return;
    }

    this.subscriberConf = subscriberApp.getSubscriberConf();
    if ( subscriberConf == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null subscriber conf" );
      return;
    }

    synchSubscriberGroupReport = new SynchSubscriberGroupReport( subscriberApp ,
        subscriberConf );
    DLog.debug( lctx , "Created synchronize subscriber group report module" );
    cleanClientSubscriberUnsubsRecord = new CleanClientSubscriberUnsubsRecord(
        subscriberApp , subscriberConf );
    DLog.debug( lctx , "Created clean client subscriber unsubs record module" );
    cleanClientSubscriberInvalidRecord = new CleanClientSubscriberInvalidRecord(
        subscriberApp , subscriberConf );
    DLog.debug( lctx , "Created clean client subscriber invalid record module" );
    cleanClientSubscriberDncRecord = new CleanClientSubscriberDncRecord(
        subscriberApp , subscriberConf );
    DLog.debug( lctx , "Created clean client subscriber dnc record module" );

    workerThread = new SubscriberManagementThread();
    activeThread = false;

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
    if ( subscriberConf.getSynchSubscriberGroupReportConf().isEnabled() ) {
      DLog.debug( lctx , "Synchronize subscriber group report "
          + "module is enabled" );
      synchSubscriberGroupReport.moduleStart();
    }
    if ( subscriberConf.getCleanClientSubscriberUnsubsRecordConf().isEnabled() ) {
      DLog.debug( lctx , "Clean client subscriber unsubs record "
          + "module is enabled" );
      cleanClientSubscriberUnsubsRecord.moduleStart();
    }
    if ( subscriberConf.getCleanClientSubscriberInvalidRecordConf().isEnabled() ) {
      DLog.debug( lctx , "Clean client subscriber invalid record "
          + "module is enabled" );
      cleanClientSubscriberInvalidRecord.moduleStart();
    }
    if ( subscriberConf.getCleanClientSubscriberDncRecordConf().isEnabled() ) {
      DLog.debug( lctx , "Clean client subscriber dnc record "
          + "module is enabled" );
      cleanClientSubscriberDncRecord.moduleStart();
    }
    activeThread = true;
    workerThread.start();
  }

  public void moduleStop() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to stop , found not yet initialized" );
      return;
    }
    cleanClientSubscriberDncRecord.moduleStop();
    cleanClientSubscriberInvalidRecord.moduleStop();
    cleanClientSubscriberUnsubsRecord.moduleStop();
    synchSubscriberGroupReport.moduleStop();
    activeThread = false;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // ////////////////////////////////////////////////////////////////////////////

  class SubscriberManagementThread extends Thread {

    public SubscriberManagementThread() {
      super( "SubscriberManagementThread" );
    }

    public void run() {
      long counter = 0 , delay1s = 1000;
      DLog.debug( lctx , "Thread started" );
      while ( activeThread ) {
        try {
          Thread.sleep( delay1s );
        } catch ( InterruptedException e ) {
        }
        counter = counter + delay1s;
        if ( counter < subscriberConf.getManagementPeriod() ) {
          continue;
        }
        counter = 0;
        // nothing to do yet ...
      }
      DLog.debug( lctx , "Thread stopped" );
    }

  }

}
