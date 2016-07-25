package com.beepcast.subscriber;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberCustomService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberCustomService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;

  private ClientSubscriberCustomDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberCustomService() {
    SubscriberApp subscriberApp = SubscriberApp.getInstance();
    if ( subscriberApp != null ) {
      debug = subscriberApp.isDebug();
    }
    dao = new ClientSubscriberCustomDAO();
  }

  public ClientSubscriberCustomService( SubscriberApp subscriberApp ) {
    if ( subscriberApp != null ) {
      debug = subscriberApp.isDebug();
    }
    dao = new ClientSubscriberCustomDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean persist( ClientSubscriberCustomBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to persist client subscriber custom bean "
          + ", found null bean" );
      return result;
    }
    ClientSubscriberCustomBean selectBean = dao.select( debug ,
        bean.getClientSubscriberId() );
    if ( selectBean == null ) {
      result = dao.insert( debug , bean );
      return result;
    }
    if ( copyBean( bean , selectBean ) ) {
      result = dao.update( debug , selectBean );
      return result;
    }
    return result;
  }

  public boolean insert( ClientSubscriberCustomBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to insert client subscriber custom bean "
          + ", found null bean" );
      return result;
    }
    result = dao.insert( debug , bean );
    return result;
  }

  public ClientSubscriberCustomBean select( int clientSubscriberId ) {
    ClientSubscriberCustomBean bean = null;
    if ( clientSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to select client subscriber custom bean "
          + ", found zero client subscriber id" );
      return bean;
    }
    bean = dao.select( debug , clientSubscriberId );
    return bean;
  }

  public boolean update( ClientSubscriberCustomBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to update client subscriber custom bean "
          + ", found null bean" );
      return result;
    }
    if ( bean.getId() < 1 ) {
      DLog.warning( lctx , "Failed to update client subscriber custom bean "
          + ", found zero id" );
      return result;
    }
    if ( bean.getClientSubscriberId() < 1 ) {
      DLog.warning( lctx , "Failed to update client subscriber custom bean "
          + ", found zero client subscriber id" );
      return result;
    }
    result = dao.update( debug , bean );
    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean copyBean( ClientSubscriberCustomBean aBean ,
      ClientSubscriberCustomBean bBean ) {
    boolean result = false;

    if ( aBean == null ) {
      return result;
    }
    if ( bBean == null ) {
      return result;
    }

    if ( aBean.getCustom0() != null ) {
      bBean.setCustom0( aBean.getCustom0() );
    }
    if ( aBean.getCustom1() != null ) {
      bBean.setCustom1( aBean.getCustom1() );
    }
    if ( aBean.getCustom2() != null ) {
      bBean.setCustom2( aBean.getCustom2() );
    }
    if ( aBean.getCustom3() != null ) {
      bBean.setCustom3( aBean.getCustom3() );
    }
    if ( aBean.getCustom4() != null ) {
      bBean.setCustom4( aBean.getCustom4() );
    }
    if ( aBean.getCustom5() != null ) {
      bBean.setCustom5( aBean.getCustom5() );
    }
    if ( aBean.getCustom6() != null ) {
      bBean.setCustom6( aBean.getCustom6() );
    }
    if ( aBean.getCustom7() != null ) {
      bBean.setCustom7( aBean.getCustom7() );
    }
    if ( aBean.getCustom8() != null ) {
      bBean.setCustom8( aBean.getCustom8() );
    }
    if ( aBean.getCustom9() != null ) {
      bBean.setCustom9( aBean.getCustom9() );
    }
    if ( aBean.getCustom10() != null ) {
      bBean.setCustom10( aBean.getCustom10() );
    }
    if ( aBean.getCustom11() != null ) {
      bBean.setCustom11( aBean.getCustom11() );
    }
    if ( aBean.getCustom12() != null ) {
      bBean.setCustom12( aBean.getCustom12() );
    }
    if ( aBean.getCustom13() != null ) {
      bBean.setCustom13( aBean.getCustom13() );
    }
    if ( aBean.getCustom14() != null ) {
      bBean.setCustom14( aBean.getCustom14() );
    }
    if ( aBean.getCustom15() != null ) {
      bBean.setCustom15( aBean.getCustom15() );
    }
    if ( aBean.getCustom16() != null ) {
      bBean.setCustom16( aBean.getCustom16() );
    }
    if ( aBean.getCustom17() != null ) {
      bBean.setCustom17( aBean.getCustom17() );
    }
    if ( aBean.getCustom18() != null ) {
      bBean.setCustom18( aBean.getCustom18() );
    }
    if ( aBean.getCustom19() != null ) {
      bBean.setCustom19( aBean.getCustom19() );
    }
    if ( aBean.getCustom20() != null ) {
      bBean.setCustom20( aBean.getCustom20() );
    }
    if ( aBean.getCustom21() != null ) {
      bBean.setCustom21( aBean.getCustom21() );
    }
    if ( aBean.getCustom22() != null ) {
      bBean.setCustom22( aBean.getCustom22() );
    }
    if ( aBean.getCustom23() != null ) {
      bBean.setCustom23( aBean.getCustom23() );
    }
    if ( aBean.getCustom24() != null ) {
      bBean.setCustom24( aBean.getCustom24() );
    }
    if ( aBean.getCustom25() != null ) {
      bBean.setCustom25( aBean.getCustom25() );
    }
    if ( aBean.getCustom26() != null ) {
      bBean.setCustom26( aBean.getCustom26() );
    }
    if ( aBean.getCustom27() != null ) {
      bBean.setCustom27( aBean.getCustom27() );
    }
    if ( aBean.getCustom28() != null ) {
      bBean.setCustom28( aBean.getCustom28() );
    }
    if ( aBean.getCustom29() != null ) {
      bBean.setCustom29( aBean.getCustom29() );
    }
    if ( aBean.getCustom30() != null ) {
      bBean.setCustom30( aBean.getCustom30() );
    }

    result = true;
    return result;
  }

}
