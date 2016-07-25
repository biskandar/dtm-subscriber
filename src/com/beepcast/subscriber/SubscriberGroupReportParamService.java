package com.beepcast.subscriber;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupReportParamService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "SubscriberGroupReportParamService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;

  private SubscriberGroupReportParamDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportParamService( SubscriberApp subscriberApp ) {
    debug = false;
    if ( subscriberApp != null ) {
      this.subscriberApp = subscriberApp;
      subscriberConf = subscriberApp.getSubscriberConf();
      debug = subscriberConf.isDebug();
    }
    dao = new SubscriberGroupReportParamDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insertBean( SubscriberGroupReportParamBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to insert subscriber group report param "
          + ", found null bean" );
      return result;
    }
    result = dao.insertBean( bean );
    return result;
  }

  public SubscriberGroupReportParamBean queryBean( int id ) {
    SubscriberGroupReportParamBean bean = null;
    if ( id < 1 ) {
      DLog.warning( lctx , "Failed to query subscriber "
          + "group report param bean , found zero id" );
      return bean;
    }
    bean = dao.queryBean( id );
    return bean;
  }

  public String getValue( int subscriberGroupId , String field ) {
    String value = null;
    if ( subscriberGroupId < 1 ) {
      return value;
    }
    SubscriberGroupReportParamBean bean = queryBean( subscriberGroupId , field );
    if ( bean == null ) {
      return value;
    }
    value = bean.getValue();
    return value;
  }

  public SubscriberGroupReportParamBean queryBean( int subscriberGroupId ,
      String field ) {
    SubscriberGroupReportParamBean bean = null;
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to query subscriber "
          + "group report param bean , found zero group id" );
      return bean;
    }
    if ( StringUtils.isBlank( field ) ) {
      DLog.warning( lctx , "Failed to query subscriber "
          + "group report param bean , found blank field" );
      return bean;
    }
    bean = dao.queryBean( subscriberGroupId , field );
    return bean;
  }

  public List queryBeans( int subscriberGroupId , String prefixField ) {
    List list = null;
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to query subscriber "
          + "group report param beans , found zero group id" );
      return list;
    }
    if ( StringUtils.isBlank( prefixField ) ) {
      DLog.warning( lctx , "Failed to query subscriber "
          + "group report param beans , found blank prefix field" );
      return list;
    }
    list = dao.queryBeans( subscriberGroupId , prefixField , debug );
    return list;
  }

  public boolean updateActiveBeanById( int id , boolean active ) {
    boolean result = false;
    if ( id < 1 ) {
      DLog.warning( lctx , "Failed to update active bean by id "
          + ", found zero id" );
      return result;
    }
    result = dao.updateActiveBeanById( id , active );
    return result;
  }

  public boolean updateActiveBeanBySubscriberGroupId( int subscriberGroupId ,
      boolean active ) {
    boolean result = false;
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to update active bean "
          + "by subscriber group id , found zero group id" );
      return result;
    }
    result = dao.updateActiveBeanBySubscriberGroupId( subscriberGroupId ,
        active );
    return result;
  }

  public int deleteAllInactiveBeans() {
    return dao.deleteAllInactiveBeans();
  }

}
