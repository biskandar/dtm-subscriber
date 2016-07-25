package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupReportService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "SubscriberGroupReportService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;
  private SubscriberGroupReportParamService sgrpService;
  private SubscriberGroupReportDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    this.subscriberConf = subscriberApp.getSubscriberConf();
    sgrpService = new SubscriberGroupReportParamService( subscriberApp );
    dao = new SubscriberGroupReportDAO( sgrpService );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportBean synchReport( SubscriberGroupBean sgBean ) {
    SubscriberGroupReportBean sgrBean = null;
    String headerLog = SubscriberGroupCommon.headerLog( sgBean );
    if ( sgBean == null ) {
      DLog.warning( lctx , headerLog + "Failed to synch report "
          + ", found null subscriber group bean" );
      return sgrBean;
    }
    int sgId = sgBean.getId();
    if ( sgId < 1 ) {
      DLog.warning( lctx , headerLog + "Failed to synch report "
          + ", found zero subscriber group id" );
      return sgrBean;
    }
    sgrBean = dao.synchReport( sgBean , subscriberConf.isDebug() );
    return sgrBean;
  }

  // ////////////////////////////////////////////////////////////////////////////

  public int totalAllNumbers( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalAllNumbers( subscriberGroupId ) );
  }

  public int totalSubscribedNumbers( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalSubscribedNumbers( subscriberGroupId ) );
  }

  public int totalUniqueNumbers( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalUniqueNumbers( subscriberGroupId ) );
  }

  public int totalSubscribedUniqueNumbers( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalSubscribedUniqueNumbers( subscriberGroupId ) );
  }

  // ////////////////////////////////////////////////////////////////////////////

  public int totalAllScheduled( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalAllScheduled( subscriberGroupId ) );
  }

  public int totalSubscribedScheduled( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalSubscribedScheduled( subscriberGroupId ) );
  }

  public int totalUniqueScheduled( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalUniqueScheduled( subscriberGroupId ) );
  }

  public int totalSubscribedUniqueScheduled( int subscriberGroupId ) {
    return totalNumbers( getMapCountryCodeTotalSubscribedUniqueScheduled( subscriberGroupId ) );
  }

  // ////////////////////////////////////////////////////////////////////////////

  public Map getMapCountryCodeTotalAllNumbers( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers( subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALALLNUMBERS );
  }

  public Map getMapCountryCodeTotalSubscribedNumbers( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers( subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALSUBSCRIBEDNUMBERS );
  }

  public Map getMapCountryCodeTotalUniqueNumbers( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers( subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALUNIQUENUMBERS );
  }

  public Map getMapCountryCodeTotalSubscribedUniqueNumbers(
      int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers(
        subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUENUMBERS );
  }

  // ////////////////////////////////////////////////////////////////////////////

  public Map getMapCountryCodeTotalAllScheduled( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers( subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALALLSCHEDULED );
  }

  public Map getMapCountryCodeTotalSubscribedScheduled( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers(
        subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALSUBSCRIBEDSCHEDULED );
  }

  public Map getMapCountryCodeTotalUniqueScheduled( int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers( subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALUNIQUESCHEDULED );
  }

  public Map getMapCountryCodeTotalSubscribedUniqueScheduled(
      int subscriberGroupId ) {
    return getMapCountryCodeTotalNumbers(
        subscriberGroupId ,
        SubscriberGroupReportParamSupport.FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUESCHEDULED );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Set / Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportParamService getSgrpService() {
    return sgrpService;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private Map getMapCountryCodeTotalNumbers( int subscriberGroupId ,
      String prefixField ) {
    Map map = new HashMap();

    String headerLog = SubscriberGroupCommon.headerLog( subscriberGroupId );

    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , headerLog
          + "Failed to get map country code total numbers "
          + ", found null subscriber group id" );
      return map;
    }
    if ( StringUtils.isBlank( prefixField ) ) {
      DLog.warning( lctx , headerLog
          + "Failed to get map country code total numbers "
          + ", found blank prefix field" );
      return map;
    }

    List listBeans = sgrpService.queryBeans( subscriberGroupId , prefixField );
    if ( listBeans == null ) {
      DLog.warning( lctx , headerLog
          + "Failed to get map country code total numbers "
          + ", found null list beans" );
      return map;
    }

    SubscriberGroupReportParamBean sgrpBean;
    Iterator iterBeans = listBeans.iterator();
    while ( iterBeans.hasNext() ) {
      sgrpBean = (SubscriberGroupReportParamBean) iterBeans.next();
      if ( sgrpBean == null ) {
        continue;
      }
      String field = sgrpBean.getField();
      if ( StringUtils.isBlank( field ) ) {
        continue;
      }
      String countryCode = SubscriberGroupReportParamSupport
          .getCountryCode( field );
      if ( StringUtils.isBlank( countryCode ) ) {
        continue;
      }
      int totalNumbers = 0;
      try {
        totalNumbers = Integer.parseInt( sgrpBean.getValue() );
      } catch ( NumberFormatException e ) {
        continue;
      }
      map.put( countryCode , new Integer( totalNumbers ) );
    }

    return map;
  }

  private int totalNumbers( Map map ) {
    int totalNumbers = 0;
    if ( map == null ) {
      return totalNumbers;
    }
    List list = new ArrayList( map.values() );
    if ( list.size() < 1 ) {
      return totalNumbers;
    }
    Iterator iter = list.iterator();
    while ( iter.hasNext() ) {
      Integer val = (Integer) iter.next();
      if ( val == null ) {
        continue;
      }
      totalNumbers = totalNumbers + val.intValue();
    }
    return totalNumbers;
  }

}
