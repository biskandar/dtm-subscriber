package com.beepcast.subscriber.view;

import java.util.Iterator;
import java.util.List;

import com.beepcast.subscriber.SubscriberApp;
import com.beepcast.subscriber.SubscriberConf;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ListGroupSubscriberViewService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ListGroupSubscriberViewService" );

  public static final int ORDERBY_IDASC = 0;
  public static final int ORDERBY_IDDESC = 1;
  public static final int ORDERBY_NAMEASC = 2;
  public static final int ORDERBY_NAMEDESC = 3;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;

  private ListGroupSubscriberViewDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ListGroupSubscriberViewService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    this.subscriberConf = subscriberApp.getSubscriberConf();

    dao = new ListGroupSubscriberViewDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ListGroupSubscriberViewBean getBean( int clientId ) {
    return getBean( clientId , 0 , 0 );
  }

  public ListGroupSubscriberViewBean getBean( int clientId , int top , int limit ) {
    return getBean( clientId , top , limit , ORDERBY_IDDESC );
  }

  public ListGroupSubscriberViewBean getBean( int clientId , int top ,
      int limit , int orderBy ) {
    return getBean( clientId , null , null , top , limit , orderBy );
  }

  public ListGroupSubscriberViewBean getBean( int clientId ,
      String inKeywordGroupNames , String exKeywordGroupNames , int top ,
      int limit , int orderBy ) {
    ListGroupSubscriberViewBean bean = ListGroupSubscriberViewFactory
        .createListGroupSubscriberViewBean( clientId , inKeywordGroupNames ,
            exKeywordGroupNames );
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to get bean "
          + ", found failed to create bean from factory" );
      return bean;
    }
    loadTotalRecords( bean );
    loadBeans( bean , top , limit , orderBy );
    return bean;
  }

  public Iterator iterBeans( ListGroupSubscriberViewBean bean ) {
    Iterator iter = null;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to generate iterator "
          + ", found null list group subscriber info bean" );
      return iter;
    }
    List groupSubscriberInfoBeans = bean.getBeans();
    if ( groupSubscriberInfoBeans == null ) {
      DLog.warning( lctx , "Failed to generate iterator "
          + ", found null group subscriber info bean" );
      return iter;
    }
    iter = groupSubscriberInfoBeans.iterator();
    return iter;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private void loadTotalRecords( ListGroupSubscriberViewBean bean ) {
    bean.setTotalRecords( dao.totalBeans( bean.getClientId() ,
        bean.getInKeywordGroupNames() , bean.getExKeywordGroupNames() ,
        subscriberConf.isDebug() ) );
  }

  private void loadBeans( ListGroupSubscriberViewBean bean , int top ,
      int limit , int orderBy ) {
    bean.setBeans( dao.selectBeans( bean.getClientId() ,
        bean.getInKeywordGroupNames() , bean.getExKeywordGroupNames() , top ,
        limit , orderBy , subscriberConf.isDebug() ) );
  }

}
