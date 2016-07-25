package com.beepcast.subscriber.view;

import com.beepcast.subscriber.SubscriberApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class GroupSubscriberViewService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "GroupSubscriberViewService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private GroupSubscriberViewDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public GroupSubscriberViewService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    dao = new GroupSubscriberViewDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public GroupSubscriberViewBean getBean( int groupSubscriberId ) {
    GroupSubscriberViewBean bean = null;

    if ( groupSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to get group subscriber bean "
          + ", found zero groupSubscriberId" );
      return bean;
    }

    bean = dao.getBean( groupSubscriberId );

    return bean;
  }

}
