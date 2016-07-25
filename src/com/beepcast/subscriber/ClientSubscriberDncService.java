package com.beepcast.subscriber;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberDncService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberDncService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private boolean debug;
  private ClientSubscriberDncDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberDncService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    if ( subscriberApp != null ) {
      this.debug = subscriberApp.isDebug();
    }
    dao = new ClientSubscriberDncDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insert( ClientSubscriberDncBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to insert , found null bean" );
      return result;
    }
    if ( bean.getClientId() < 1 ) {
      DLog.warning( lctx , "Failed to insert , found zero bean's clientId" );
      return result;
    }
    if ( StringUtils.isBlank( bean.getPhone() ) ) {
      DLog.warning( lctx , "Failed to insert , found blank bean's phone" );
      return result;
    }
    result = dao.insert( debug , bean );
    return result;
  }

  public ClientSubscriberDncBean selectByClientIdPhoneNumber( int clientId ,
      String phoneNumber ) {
    ClientSubscriberDncBean bean = null;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to select , found zero clientId" );
      return bean;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to select , found blank phoneNumber" );
      return bean;
    }
    bean = dao.selectByClientIdPhoneNumber( debug , clientId , phoneNumber );
    return bean;
  }

  public ClientSubscriberDncBean selectFirstActiveRecordByClientId( int clientId ) {
    ClientSubscriberDncBean bean = null;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to select first active record "
          + ", found zero clientId" );
      return bean;
    }
    bean = dao.selectFirstActiveRecordByClientId( debug , clientId );
    return bean;
  }

  public boolean deleteById( int id ) {
    boolean result = false;
    if ( id < 1 ) {
      DLog.warning( lctx , "Failed to delete , found zero id" );
      return result;
    }
    result = dao.deleteById( debug , id );
    return result;
  }

  public long deleteByClientIdSynchNo( int clientId , int synchNo ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to delete , found zero clientId" );
      return totalRecords;
    }
    if ( synchNo < 0 ) {
      DLog.warning( lctx , "Failed to delete , found negatif synchNo" );
      return totalRecords;
    }
    totalRecords = dao.deleteByClientIdSynchNo( debug , clientId , synchNo );
    return totalRecords;
  }

  public long deleteByClientIdPhoneNumber( int clientId , String phoneNumber ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to delete , found zero clientId" );
      return totalRecords;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to delete , found blank phoneNumber" );
      return totalRecords;
    }
    totalRecords = dao.deleteByClientIdPhoneNumber( debug , clientId ,
        phoneNumber );
    return totalRecords;
  }

  public long selectTotalByClientIdActive( int clientId , boolean active ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to select total by clientId Active "
          + ", found zero clientId" );
      return totalRecords;
    }
    totalRecords = dao.selectTotalByClientIdActive( debug , clientId , active );
    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  // ...

}
