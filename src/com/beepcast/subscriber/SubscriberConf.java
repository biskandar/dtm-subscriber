package com.beepcast.subscriber;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberConf {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberConf" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;
  private int managementPeriod;
  private SynchSubscriberGroupReportConf synchSubscriberGroupReportConf;
  private CleanClientSubscriberUnsubsRecordConf cleanClientSubscriberUnsubsRecordConf;
  private CleanClientSubscriberInvalidRecordConf cleanClientSubscriberInvalidRecordConf;
  private CleanClientSubscriberDncRecordConf cleanClientSubscriberDncRecordConf;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////
  public SubscriberConf() {
    init();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void init() {
    debug = false;

    managementPeriod = 5000;

    synchSubscriberGroupReportConf = new SynchSubscriberGroupReportConf();
    synchSubscriberGroupReportConf.setEnabled( false );
    synchSubscriberGroupReportConf.setLimitRecords( 100 );
    synchSubscriberGroupReportConf.setSleepPerRecordInSeconds( 5 );
    synchSubscriberGroupReportConf.setSleepPerBatchInSeconds( 10 );
    synchSubscriberGroupReportConf.setSleepInitializedInSeconds( 300 );
    synchSubscriberGroupReportConf.setLastDays( 90 );

    cleanClientSubscriberUnsubsRecordConf = new CleanClientSubscriberUnsubsRecordConf();
    cleanClientSubscriberUnsubsRecordConf.setEnabled( false );
    cleanClientSubscriberUnsubsRecordConf.setLimitRecords( 100 );
    cleanClientSubscriberUnsubsRecordConf.setSleepPerRecordInSeconds( 5 );
    cleanClientSubscriberUnsubsRecordConf.setSleepPerBatchInSeconds( 10 );
    cleanClientSubscriberUnsubsRecordConf.setSleepInitializedInSeconds( 300 );

    cleanClientSubscriberInvalidRecordConf = new CleanClientSubscriberInvalidRecordConf();
    cleanClientSubscriberInvalidRecordConf.setEnabled( false );
    cleanClientSubscriberInvalidRecordConf.setLimitRecords( 100 );
    cleanClientSubscriberInvalidRecordConf.setSleepPerRecordInSeconds( 5 );
    cleanClientSubscriberInvalidRecordConf.setSleepPerBatchInSeconds( 10 );
    cleanClientSubscriberInvalidRecordConf.setSleepInitializedInSeconds( 300 );

    cleanClientSubscriberDncRecordConf = new CleanClientSubscriberDncRecordConf();
    cleanClientSubscriberDncRecordConf.setEnabled( false );
    cleanClientSubscriberDncRecordConf.setLimitRecords( 100 );
    cleanClientSubscriberDncRecordConf.setSleepPerRecordInSeconds( 5 );
    cleanClientSubscriberDncRecordConf.setSleepPerBatchInSeconds( 10 );
    cleanClientSubscriberDncRecordConf.setSleepInitializedInSeconds( 300 );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean isDebug() {
    return debug;
  }

  public void setDebug( boolean debug ) {
    this.debug = debug;
  }

  public int getManagementPeriod() {
    return managementPeriod;
  }

  public void setManagementPeriod( int managementPeriod ) {
    this.managementPeriod = managementPeriod;
  }

  public SynchSubscriberGroupReportConf getSynchSubscriberGroupReportConf() {
    return synchSubscriberGroupReportConf;
  }

  public CleanClientSubscriberUnsubsRecordConf getCleanClientSubscriberUnsubsRecordConf() {
    return cleanClientSubscriberUnsubsRecordConf;
  }

  public CleanClientSubscriberInvalidRecordConf getCleanClientSubscriberInvalidRecordConf() {
    return cleanClientSubscriberInvalidRecordConf;
  }

  public CleanClientSubscriberDncRecordConf getCleanClientSubscriberDncRecordConf() {
    return cleanClientSubscriberDncRecordConf;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // ////////////////////////////////////////////////////////////////////////////

  public class SynchSubscriberGroupReportConf extends ManagementTaskConf {

    private int lastDays;

    public SynchSubscriberGroupReportConf() {
      super();
    }

    public int getLastDays() {
      return lastDays;
    }

    public void setLastDays( int lastDays ) {
      this.lastDays = lastDays;
    }

  }

  public class CleanClientSubscriberUnsubsRecordConf extends ManagementTaskConf {

    public CleanClientSubscriberUnsubsRecordConf() {
      super();
    }

  }

  public class CleanClientSubscriberInvalidRecordConf extends
      ManagementTaskConf {

    public CleanClientSubscriberInvalidRecordConf() {
      super();
    }

  }

  public class CleanClientSubscriberDncRecordConf extends ManagementTaskConf {

    public CleanClientSubscriberDncRecordConf() {
      super();
    }

  }

  public class ManagementTaskConf {

    private boolean enabled;
    private int limitRecords;
    private long sleepPerRecordInSeconds;
    private long sleepPerBatchInSeconds;
    private long sleepInitializedInSeconds;

    public ManagementTaskConf() {

    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled( boolean enabled ) {
      this.enabled = enabled;
    }

    public int getLimitRecords() {
      return limitRecords;
    }

    public void setLimitRecords( int limitRecords ) {
      this.limitRecords = limitRecords;
    }

    public long getSleepPerRecordInSeconds() {
      return sleepPerRecordInSeconds;
    }

    public void setSleepPerRecordInSeconds( long sleepPerRecordInSeconds ) {
      this.sleepPerRecordInSeconds = sleepPerRecordInSeconds;
    }

    public long getSleepPerBatchInSeconds() {
      return sleepPerBatchInSeconds;
    }

    public void setSleepPerBatchInSeconds( long sleepPerBatchInSeconds ) {
      this.sleepPerBatchInSeconds = sleepPerBatchInSeconds;
    }

    public long getSleepInitializedInSeconds() {
      return sleepInitializedInSeconds;
    }

    public void setSleepInitializedInSeconds( long sleepInitializedInSeconds ) {
      this.sleepInitializedInSeconds = sleepInitializedInSeconds;
    }

  } // public class ManagementTaskConf

}
