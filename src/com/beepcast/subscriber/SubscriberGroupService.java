package com.beepcast.subscriber;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupService implements SubscriberGroupApi {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberGroupService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;

  private SubscriberGroupDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    this.subscriberConf = subscriberApp.getSubscriberConf();

    dao = new SubscriberGroupDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupBean getSubscriberGroupBean( int subscriberGroupId ) {
    SubscriberGroupBean bean = null;
    if ( subscriberGroupId > 0 ) {
      bean = dao.getSubscriberGroupBean( subscriberGroupId ,
          subscriberConf.isDebug() );
    } else {
      DLog.warning( lctx , "Failed to get subscriber group bean "
          + ", found zero subscriberGroupId" );
    }
    return bean;
  }

  public SubscriberGroupBean getSubscriberGroupBean( int clientId ,
      String groupName ) {
    SubscriberGroupBean bean = null;
    if ( ( groupName != null ) && ( !groupName.equals( "" ) ) ) {
      bean = dao.getSubscriberGroupBean( clientId , groupName ,
          subscriberConf.isDebug() );
    } else {
      DLog.warning( lctx , "Failed to get subscriber group bean "
          + ", found zero groupName" );
    }
    return bean;
  }

  public List listSubscriberGroupBeans( int clientId ) {
    List list = null;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to list subscriber group bean "
          + ", found zero client id" );
      return list;
    }
    list = dao.listSubscriberGroupBeans( clientId , subscriberConf.isDebug() );
    return list;
  }

  public SubscriberGroupBean insertSubscriberGroup( int clientId ,
      String groupName ) {
    SubscriberGroupBean bean = null;
    if ( ( clientId > 0 ) && ( groupName != null )
        && ( !groupName.equals( "" ) ) ) {
      bean = dao.insertSubscriberGroup( clientId , groupName ,
          subscriberConf.isDebug() );
    } else {
      DLog.warning( lctx , "Failed to insert SubscriberGroup "
          + ", found zero groupName and/or clientId" );
    }
    return bean;
  }

  public boolean updateSubscriberGroup( int subscriberGroupId , String groupName ) {
    boolean result = false;
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to update subscriber group "
          + ", found zero subscriberGroupId" );
      return result;
    }
    if ( StringUtils.isBlank( groupName ) ) {
      DLog.warning( lctx , "Failed to update subscriber group "
          + ", found blank groupName" );
      return result;
    }
    result = dao.updateSubscriberGroup( subscriberGroupId , groupName ,
        subscriberConf.isDebug() );
    return result;
  }

  public boolean setInActiveSubscriberGroup( int subscriberGroupId ) {
    boolean result = false;
    if ( subscriberGroupId > 0 ) {
      result = dao.setInActiveSubscriberGroup( subscriberGroupId ,
          subscriberConf.isDebug() );
    } else {
      DLog.warning( lctx , "Failed to inactive status at SubscriberGroup "
          + ", found zero subscriberGroupId" );
    }
    return result;
  }

  public int deleteNumbersInSubscriberGroup( int subscriberGroupId ) {
    int result = 0;
    if ( subscriberGroupId > 0 ) {
      result = dao.deleteNumbersInSubscriberGroup( subscriberGroupId ,
          subscriberConf.isDebug() );
    } else {
      DLog.warning( lctx , "Failed to delete numbers in SubscriberGroup "
          + ", found zero subscriberGroupId" );
    }
    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List listSubscriberGroupBeans( int top , int limit ) {
    return dao.listSubscriberGroupBeans( 0 , 0 , true , top , limit ,
        subscriberConf.isDebug() );
  }

  public List listSubscriberGroupBeans( boolean orderIdAsc , int top , int limit ) {
    return dao.listSubscriberGroupBeans( 0 , 0 , orderIdAsc , top , limit ,
        subscriberConf.isDebug() );
  }

  public List listSubscriberGroupBeansByClientId( int clientId ,
      boolean orderIdAsc , int top , int limit ) {
    return dao.listSubscriberGroupBeans( clientId , 0 , orderIdAsc , top ,
        limit , subscriberConf.isDebug() );
  }

  public List listSubscriberGroupBeansByLastDays( int lastDays ,
      boolean orderIdAsc , int top , int limit ) {
    return dao.listSubscriberGroupBeans( 0 , lastDays , orderIdAsc , top ,
        limit , subscriberConf.isDebug() );
  }

  public List listSubscriberGroupBeans( int clientId , int lastDays ,
      boolean orderIdAsc , int top , int limit ) {
    return dao.listSubscriberGroupBeans( clientId , lastDays , orderIdAsc ,
        top , limit , subscriberConf.isDebug() );
  }

  public long getTotalSubscriberGroup() {
    return dao.getTotalSubscriberGroup( 0 , 0 , subscriberConf.isDebug() );
  }

  public long getTotalSubscriberGroupByClientId( int clientId ) {
    return dao
        .getTotalSubscriberGroup( clientId , 0 , subscriberConf.isDebug() );
  }

  public long getTotalSubscriberGroupByLastDays( int lastDays ) {
    return dao
        .getTotalSubscriberGroup( 0 , lastDays , subscriberConf.isDebug() );
  }

  public long getTotalSubscriberGroup( int clientId , int lastDays ) {
    return dao.getTotalSubscriberGroup( clientId , lastDays ,
        subscriberConf.isDebug() );
  }

  public long getTotalSubscriberGroupNumber( int clientId ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to get total subscriber group "
          + ", found zero client id" );
      return totalRecords;
    }
    totalRecords = dao.getTotalSubscriberGroupNumber( clientId ,
        subscriberConf.isDebug() );
    return totalRecords;
  }

}
