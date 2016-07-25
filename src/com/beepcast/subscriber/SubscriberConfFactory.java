package com.beepcast.subscriber;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.beepcast.subscriber.SubscriberConf.CleanClientSubscriberDncRecordConf;
import com.beepcast.subscriber.SubscriberConf.CleanClientSubscriberInvalidRecordConf;
import com.beepcast.subscriber.SubscriberConf.CleanClientSubscriberUnsubsRecordConf;
import com.beepcast.subscriber.SubscriberConf.ManagementTaskConf;
import com.beepcast.subscriber.SubscriberConf.SynchSubscriberGroupReportConf;
import com.beepcast.util.properties.GlobalEnvironment;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.firsthop.common.util.xml.TreeUtil;

public class SubscriberConfFactory {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberConfFactory" );

  static final GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public static SubscriberConf generateSubscriberConf(
      String propertyFileSubscriber ) {
    SubscriberConf subscriberConf = new SubscriberConf();

    if ( StringUtils.isBlank( propertyFileSubscriber ) ) {
      return subscriberConf;
    }

    DLog.debug( lctx , "Loading from property = " + propertyFileSubscriber );

    Element element = globalEnv.getElement( SubscriberConf.class.getName() ,
        propertyFileSubscriber );
    if ( element != null ) {
      boolean result = validateTag( element );
      if ( result ) {
        extractElement( element , subscriberConf );
      }
    }

    return subscriberConf;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static boolean validateTag( Element element ) {
    boolean result = false;

    if ( element == null ) {
      DLog.warning( lctx , "Found empty in element xml" );
      return result;
    }

    Node node = TreeUtil.first( element , "subscriber" );
    if ( node == null ) {
      DLog.warning( lctx , "Can not find root tag <subscriber>" );
      return result;
    }

    result = true;
    return result;
  }

  private static boolean extractElement( Element element ,
      SubscriberConf subscriberConf ) {
    boolean result = false;

    String stemp;

    Node nodeSubscriber = TreeUtil.first( element , "subscriber" );
    if ( nodeSubscriber == null ) {
      return result;
    }

    stemp = TreeUtil.getAttribute( nodeSubscriber , "debug" );
    if ( StringUtils.equalsIgnoreCase( stemp , "true" ) ) {
      subscriberConf.setDebug( true );
    }

    // extract management
    Node nodeManagement = TreeUtil.first( nodeSubscriber , "management" );
    extractNodeManagement( nodeManagement , subscriberConf );

    result = true;
    return result;
  }

  private static boolean extractNodeManagement( Node node ,
      SubscriberConf subscriberConf ) {
    boolean result = false;
    if ( node == null ) {
      return result;
    }

    String stemp;

    stemp = TreeUtil.getAttribute( node , "period" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        subscriberConf.setManagementPeriod( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    Node nodeSynchSubscriberGroupReport = TreeUtil.first( node ,
        "synchSubscriberGroupReport" );
    if ( nodeSynchSubscriberGroupReport != null ) {
      SynchSubscriberGroupReportConf synchSubscriberGroupReportConf = subscriberConf
          .getSynchSubscriberGroupReportConf();
      extractNodeManagementTask( nodeSynchSubscriberGroupReport ,
          synchSubscriberGroupReportConf );
      stemp = TreeUtil.getAttribute( nodeSynchSubscriberGroupReport ,
          "lastDays" );
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        try {
          synchSubscriberGroupReportConf
              .setLastDays( Integer.parseInt( stemp ) );
        } catch ( NumberFormatException e ) {
        }
      }
    }

    {
      Node nodeCleanClientSubscriberUnsubsRecord = TreeUtil.first( node ,
          "cleanClientSubscriberUnsubsRecord" );
      if ( nodeCleanClientSubscriberUnsubsRecord != null ) {
        CleanClientSubscriberUnsubsRecordConf cleanClientSubscriberUnsubsRecordConf = subscriberConf
            .getCleanClientSubscriberUnsubsRecordConf();
        extractNodeManagementTask( nodeCleanClientSubscriberUnsubsRecord ,
            cleanClientSubscriberUnsubsRecordConf );
      }
    }

    {
      Node nodeCleanClientSubscriberInvalidRecord = TreeUtil.first( node ,
          "cleanClientSubscriberInvalidRecord" );
      if ( nodeCleanClientSubscriberInvalidRecord != null ) {
        CleanClientSubscriberInvalidRecordConf cleanClientSubscriberInvalidRecordConf = subscriberConf
            .getCleanClientSubscriberInvalidRecordConf();
        extractNodeManagementTask( nodeCleanClientSubscriberInvalidRecord ,
            cleanClientSubscriberInvalidRecordConf );
      }
    }

    {
      Node nodeCleanClientSubscriberDncRecord = TreeUtil.first( node ,
          "cleanClientSubscriberDncRecord" );
      if ( nodeCleanClientSubscriberDncRecord != null ) {
        CleanClientSubscriberDncRecordConf cleanClientSubscriberDncRecordConf = subscriberConf
            .getCleanClientSubscriberDncRecordConf();
        extractNodeManagementTask( nodeCleanClientSubscriberDncRecord ,
            cleanClientSubscriberDncRecordConf );
      }
    }

    result = true;
    return result;
  }

  private static boolean extractNodeManagementTask( Node nodeManagementTask ,
      ManagementTaskConf managementTaskConf ) {
    boolean result = false;

    if ( managementTaskConf == null ) {
      return result;
    }

    String stemp = null;

    stemp = TreeUtil.getAttribute( nodeManagementTask , "enabled" );
    if ( StringUtils.equalsIgnoreCase( stemp , "true" ) ) {
      managementTaskConf.setEnabled( true );
    }

    stemp = TreeUtil.getAttribute( nodeManagementTask , "limitRecords" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        managementTaskConf.setLimitRecords( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = TreeUtil.getAttribute( nodeManagementTask ,
        "sleepPerRecordInSeconds" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        managementTaskConf.setSleepPerRecordInSeconds( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = TreeUtil.getAttribute( nodeManagementTask ,
        "sleepPerBatchInSeconds" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        managementTaskConf.setSleepPerBatchInSeconds( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = TreeUtil.getAttribute( nodeManagementTask ,
        "sleepInitializedInSeconds" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        managementTaskConf
            .setSleepInitializedInSeconds( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    result = true;
    return result;
  }

}
