package com.beepcast.subscriber.view;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ListGroupSubscriberViewFactory {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ListGroupSubscriberViewFactory" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public static ListGroupSubscriberViewBean createListGroupSubscriberViewBean(
      int clientId , String inKeywordGroupNames , String exKeywordGroupNames ) {
    ListGroupSubscriberViewBean bean = new ListGroupSubscriberViewBean();
    bean.setClientId( clientId );
    bean.setInKeywordGroupNames( inKeywordGroupNames );
    bean.setExKeywordGroupNames( exKeywordGroupNames );
    bean.setTotalRecords( 0 );
    bean.setBeans( null );
    return bean;
  }

}
