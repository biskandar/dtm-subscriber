package com.beepcast.subscriber;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberUnsubsService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberUnsubsService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private boolean debug;
  private ClientSubscriberUnsubsDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberUnsubsService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    if ( subscriberApp != null ) {
      this.debug = subscriberApp.isDebug();
    }
    dao = new ClientSubscriberUnsubsDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberUnsubsBean queryClientSubscriberUnsubsBean(
      int clientId , String phoneNumber ) {
    ClientSubscriberUnsubsBean bean = null;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to query client subscriber unsubs bean "
          + ", found zero client id" );
      return bean;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to query client subscriber unsubs bean "
          + ", found null phone number" );
      return bean;
    }
    bean = dao.queryClientSubscriberUnsubsBean( debug , clientId , phoneNumber );
    return bean;
  }

  public boolean isPhoneExist( int clientId , String phoneNumber ) {
    boolean result = false;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to check phone exist "
          + ", found zero client id" );
      return result;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to check phone exist "
          + ", found null phone number" );
      return result;
    }
    result = dao.isPhoneExist( debug , clientId , phoneNumber );
    return result;
  }

  public boolean insertNewPhone( int clientId , String phoneNumber ,
      int fromEventId ) {
    boolean result = false;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to insert new phone number "
          + ", found zero client id" );
      return result;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to insert new phone number "
          + ", found null phone number" );
      return result;
    }
    result = dao.insertNewPhone( debug , clientId , phoneNumber , fromEventId );
    return result;
  }

  public boolean deletePhone( int clientId , String phoneNumber ) {
    boolean result = false;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to delete phone number "
          + ", found zero client id" );
      return result;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to delete phone number "
          + ", found null phone number" );
      return result;
    }
    result = dao.deletePhone( debug , clientId , phoneNumber );
    return result;
  }

  public long totalActiveNumbers( int clientId ) {
    long totalNumbers = 0;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to calculate total number "
          + ", found zero client id" );
      return totalNumbers;
    }
    totalNumbers = dao.totalActiveNumbers( debug , clientId );
    return totalNumbers;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberService getClientSubscriberService() {
    return subscriberApp.getClientSubscriberService();
  }

}
