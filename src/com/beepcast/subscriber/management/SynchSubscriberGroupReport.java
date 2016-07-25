package com.beepcast.subscriber.management;

import java.util.ArrayList;
import java.util.List;

import com.beepcast.subscriber.Module;
import com.beepcast.subscriber.SubscriberApp;
import com.beepcast.subscriber.SubscriberConf;
import com.beepcast.subscriber.SubscriberConf.SynchSubscriberGroupReportConf;
import com.beepcast.subscriber.SubscriberGroupBean;
import com.beepcast.subscriber.SubscriberGroupReportBean;
import com.beepcast.subscriber.SubscriberGroupReportParamService;
import com.beepcast.subscriber.SubscriberGroupReportService;
import com.beepcast.subscriber.SubscriberGroupService;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SynchSubscriberGroupReport implements Module {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "SynchSubscriberGroupReport" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean initialized;

  private SubscriberApp app;
  private SubscriberConf conf;
  private SynchSubscriberGroupReportConf ssgrConf;

  private SubscriberGroupService sgService;
  private SubscriberGroupReportService sgrService;
  private SubscriberGroupReportParamService sgrpService;

  private Thread workerThread;
  private boolean activeThread;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SynchSubscriberGroupReport( SubscriberApp app , SubscriberConf conf ) {
    initialized = false;

    if ( app == null ) {
      return;
    }
    if ( conf == null ) {
      return;
    }

    this.app = app;
    this.conf = conf;
    this.ssgrConf = conf.getSynchSubscriberGroupReportConf();

    if ( ssgrConf == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null synchronized subscriber group report conf" );
      return;
    }

    sgService = app.getSubscriberGroupService();
    if ( sgService == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null subscriber group service" );
      return;
    }
    DLog.debug( lctx , "Prepared subscriber group service" );

    sgrService = app.getSubscriberGroupReportService();
    if ( sgrService == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null subscriber group report service" );
      return;
    }
    DLog.debug( lctx , "Prepared subscriber group report service" );

    sgrpService = sgrService.getSgrpService();
    if ( sgrService == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null subscriber group report param service" );
      return;
    }
    DLog.debug( lctx , "Prepared subscriber group report param service" );

    // setup worker thread
    workerThread = new SynchSubscriberGroupReportThread();
    activeThread = false;

    // log it
    DLog.debug(
        lctx ,
        "Initialized : limitRecords = " + ssgrConf.getLimitRecords()
            + " , sleepPerRecordInSeconds = "
            + ssgrConf.getSleepPerRecordInSeconds()
            + " , sleepPerBatchInSeconds = "
            + ssgrConf.getSleepPerBatchInSeconds()
            + " , sleepInitializedInSeconds = "
            + ssgrConf.getSleepInitializedInSeconds() + " , lastDays = "
            + ssgrConf.getLastDays() );

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

  // ...

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // ////////////////////////////////////////////////////////////////////////////

  class SynchSubscriberGroupReportThread extends Thread {

    public SynchSubscriberGroupReportThread() {
      super( "SynchSubscriberGroupReportThread" );
    }

    public void run() {
      long counter = 0 , delay1s = 1000;
      long deltaTimeProcessedReports = 0;
      int totalProcessedReports = 0 , totalAvailableReports = 0 , indexBatchedReports = 0;
      int topRecords = 0 , limitRecords = 0;
      SubscriberGroupBean sgBean = null;
      SubscriberGroupReportBean sgrBean = null;
      List listSubscriberGroupBeans = new ArrayList();
      DLog.debug( lctx , "Thread started" );
      while ( activeThread ) {

        // sleep every 1 second
        try {
          Thread.sleep( delay1s );
        } catch ( InterruptedException e ) {
        }
        counter = counter + delay1s;

        // process all bean in the listSubscriberGroupBeans
        if ( counter % ( delay1s * ssgrConf.getSleepPerRecordInSeconds() ) == 0 ) {
          if ( listSubscriberGroupBeans.size() > 0 ) {
            sgBean = (SubscriberGroupBean) listSubscriberGroupBeans.remove( 0 );
            if ( sgBean != null ) {
              long deltaTimePerRecord = System.currentTimeMillis();
              sgrBean = sgrService.synchReport( sgBean );
              deltaTimePerRecord = System.currentTimeMillis()
                  - deltaTimePerRecord;
              if ( sgrBean != null ) {
                totalProcessedReports = totalProcessedReports + 1;
                deltaTimeProcessedReports = deltaTimeProcessedReports
                    + deltaTimePerRecord;
              }
            }
          }
        }

        // generate the listSubscriberGroupBeans when found empty
        if ( counter % ( delay1s * ssgrConf.getSleepPerBatchInSeconds() ) == 0 ) {
          if ( ( limitRecords > 0 ) && ( listSubscriberGroupBeans.size() < 1 ) ) {
            List listSelectedBeans = sgService
                .listSubscriberGroupBeansByLastDays( ssgrConf.getLastDays() ,
                    false , topRecords , limitRecords );
            if ( ( listSelectedBeans != null )
                && ( listSelectedBeans.size() > 0 ) ) {
              listSubscriberGroupBeans.addAll( listSelectedBeans );
              // log periodically
              if ( conf.isDebug() ) {
                DLog.debug( lctx , "Loop #" + indexBatchedReports
                    + " synchronize subscriber group report : processed = "
                    + totalProcessedReports + " report(s) ( took = "
                    + deltaTimeProcessedReports + " ms ) , new indexed = "
                    + topRecords + " record(s) , new buffered = "
                    + listSubscriberGroupBeans.size() + " record(s)" );
              }
              topRecords = topRecords + limitRecords;
              indexBatchedReports = indexBatchedReports + 1;
            } else {
              limitRecords = 0;
              // clean report params table
              if ( sgrpService.deleteAllInactiveBeans() > 0 ) {
                DLog.debug( lctx , "Clean inactive subscriber group report "
                    + " param" );
              }
              // log end
              DLog.debug( lctx , "End synchronize subscriber group report "
                  + ": processed = " + totalProcessedReports
                  + " report(s) ( took = " + deltaTimeProcessedReports
                  + " ms ) , last indexed = " + topRecords );
            }
            totalProcessedReports = 0;
            deltaTimeProcessedReports = 0;
          }
        }

        // initiate to start , stop and restart the process
        if ( counter % ( delay1s * ssgrConf.getSleepInitializedInSeconds() ) == 0 ) {

          // will restart the report when found a new list
          int totalAvailableReportsNew = (int) sgService
              .getTotalSubscriberGroupByLastDays( ssgrConf.getLastDays() );
          boolean restart = false;
          if ( totalAvailableReports != totalAvailableReportsNew ) {
            restart = true;
            DLog.debug( lctx , "Found new subscriber group(s) : total = "
                + totalAvailableReports + " -> " + totalAvailableReportsNew
                + " , restart = " + restart );
          }
          totalAvailableReports = totalAvailableReportsNew;

          // will do something when found any candidate records
          if ( totalAvailableReports > 0 ) {
            if ( ( restart )
                || ( ( listSubscriberGroupBeans.size() < 1 ) && ( limitRecords < 1 ) ) ) {
              if ( restart ) {
                // force to clean the buffer
                DLog.debug( lctx , "Need to restart , force to clear : "
                    + " buffered = " + listSubscriberGroupBeans.size()
                    + " -> 0 record(s) , processed = " + totalProcessedReports
                    + " -> 0 report(s) ( took = " + deltaTimeProcessedReports
                    + " ms )" );
                listSubscriberGroupBeans.clear();
              }
              // start the report , reset params
              topRecords = 0;
              limitRecords = ssgrConf.getLimitRecords();
              indexBatchedReports = 0;
              totalProcessedReports = 0;
              deltaTimeProcessedReports = 0;
              DLog.debug( lctx , "Start synchronize subscriber group "
                  + "report : total = " + totalAvailableReports
                  + " report(s) , limit = " + limitRecords + " record(s)" );
            }
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
