package com.beepcast.subscriber.view;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beepcast.subscriber.SubscriberApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberViewService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberViewService" );

  public static final int MAX_LIMIT_RECORDS = 500;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;

  private ClientSubscriberViewDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberViewService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;

    dao = new ClientSubscriberViewDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List searchByPhoneNumber( int clientId , String phoneNumber , int limit ) {
    List list = null;

    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to search by phoneNumber "
          + ", found zero clientId" );
      return list;
    }

    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to search by phoneNumber "
          + ", found blank phoneNumber" );
      return list;
    }

    limit = ( limit > MAX_LIMIT_RECORDS ) ? MAX_LIMIT_RECORDS : limit;
    DLog.warning( lctx , "Found limit is above than " + MAX_LIMIT_RECORDS
        + " , force the limit into default" );

    boolean debug = false;
    if ( subscriberApp != null ) {
      debug = subscriberApp.isDebug();
    }

    list = dao.searchByPhoneNumber( debug , clientId , phoneNumber , limit );

    return list;
  }

}
