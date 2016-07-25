package com.beepcast.subscriber;

import java.util.Date;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberFactory {

  static final DLogContext lctx = new SimpleContext( "ClientSubscriberFactory" );

  public static ClientSubscriberBean createClientSubscriberBean( int clientId ,
      int subscriberGroupId , String phoneNumber , String customerReferenceId ,
      String customerReferenceCode ) {
    ClientSubscriberBean bean = new ClientSubscriberBean();
    bean.setClientId( clientId );
    bean.setSubscriberGroupId( subscriberGroupId );
    bean.setPhone( phoneNumber );
    bean.setSubscribed( 1 );
    bean.setDateSubscribed( new Date() );
    bean.setGlobalSubscribed( 1 );
    bean.setDateGlobalSubscribed( new Date() );
    bean.setCustomerReferenceId( customerReferenceId );
    bean.setCustomerReferenceCode( customerReferenceCode );
    bean.setActive( 1 );
    bean.setCsCustomBean( createClientSubscriberCustomBean( 0 ) );
    return bean;
  }

  public static ClientSubscriberCustomBean createClientSubscriberCustomBean(
      int clientSubscriberId , String[] arrCustoms ) {
    ClientSubscriberCustomBean bean = createClientSubscriberCustomBean( clientSubscriberId );
    updateCustoms( arrCustoms , bean );
    return bean;
  }

  public static ClientSubscriberCustomBean createClientSubscriberCustomBean(
      int clientSubscriberId ) {
    return createClientSubscriberCustomBean( clientSubscriberId , null , null ,
        null , null , null , null , null , null , null , null , null , null ,
        null , null , null , null , null , null , null , null , null , null ,
        null , null , null , null , null , null , null , null , null );
  }

  public static ClientSubscriberCustomBean createClientSubscriberCustomBean(
      int clientSubscriberId , String custom0 , String custom1 ,
      String custom2 , String custom3 , String custom4 , String custom5 ,
      String custom6 , String custom7 , String custom8 , String custom9 ,
      String custom10 , String custom11 , String custom12 , String custom13 ,
      String custom14 , String custom15 , String custom16 , String custom17 ,
      String custom18 , String custom19 , String custom20 , String custom21 ,
      String custom22 , String custom23 , String custom24 , String custom25 ,
      String custom26 , String custom27 , String custom28 , String custom29 ,
      String custom30 ) {
    ClientSubscriberCustomBean bean = new ClientSubscriberCustomBean();
    bean.setClientSubscriberId( clientSubscriberId );
    bean.setCustom0( custom0 );
    bean.setCustom1( custom1 );
    bean.setCustom2( custom2 );
    bean.setCustom3( custom3 );
    bean.setCustom4( custom4 );
    bean.setCustom5( custom5 );
    bean.setCustom6( custom6 );
    bean.setCustom7( custom7 );
    bean.setCustom8( custom8 );
    bean.setCustom9( custom9 );
    bean.setCustom10( custom10 );
    bean.setCustom11( custom11 );
    bean.setCustom12( custom12 );
    bean.setCustom13( custom13 );
    bean.setCustom14( custom14 );
    bean.setCustom15( custom15 );
    bean.setCustom16( custom16 );
    bean.setCustom17( custom17 );
    bean.setCustom18( custom18 );
    bean.setCustom19( custom19 );
    bean.setCustom20( custom20 );
    bean.setCustom21( custom21 );
    bean.setCustom22( custom22 );
    bean.setCustom23( custom23 );
    bean.setCustom24( custom24 );
    bean.setCustom25( custom25 );
    bean.setCustom26( custom26 );
    bean.setCustom27( custom27 );
    bean.setCustom28( custom28 );
    bean.setCustom29( custom29 );
    bean.setCustom30( custom30 );
    return bean;
  }

  public static boolean updateCustoms( String[] arrCustoms ,
      ClientSubscriberCustomBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      return result;
    }
    if ( arrCustoms == null ) {
      return result;
    }
    for ( int idx = 0 ; idx < arrCustoms.length ; idx++ ) {
      String custom = arrCustoms[idx];
      if ( custom == null ) {
        continue;
      }
      switch ( idx ) {
      case 0 :
        bean.setCustom0( custom );
        break;
      case 1 :
        bean.setCustom1( custom );
        break;
      case 2 :
        bean.setCustom2( custom );
        break;
      case 3 :
        bean.setCustom3( custom );
        break;
      case 4 :
        bean.setCustom4( custom );
        break;
      case 5 :
        bean.setCustom5( custom );
        break;
      case 6 :
        bean.setCustom6( custom );
        break;
      case 7 :
        bean.setCustom7( custom );
        break;
      case 8 :
        bean.setCustom8( custom );
        break;
      case 9 :
        bean.setCustom9( custom );
        break;
      case 10 :
        bean.setCustom10( custom );
        break;
      case 11 :
        bean.setCustom11( custom );
        break;
      case 12 :
        bean.setCustom12( custom );
        break;
      case 13 :
        bean.setCustom13( custom );
        break;
      case 14 :
        bean.setCustom14( custom );
        break;
      case 15 :
        bean.setCustom15( custom );
        break;
      case 16 :
        bean.setCustom16( custom );
        break;
      case 17 :
        bean.setCustom17( custom );
        break;
      case 18 :
        bean.setCustom18( custom );
        break;
      case 19 :
        bean.setCustom19( custom );
        break;
      case 20 :
        bean.setCustom20( custom );
        break;
      case 21 :
        bean.setCustom21( custom );
        break;
      case 22 :
        bean.setCustom22( custom );
        break;
      case 23 :
        bean.setCustom23( custom );
        break;
      case 24 :
        bean.setCustom24( custom );
        break;
      case 25 :
        bean.setCustom25( custom );
        break;
      case 26 :
        bean.setCustom26( custom );
        break;
      case 27 :
        bean.setCustom27( custom );
        break;
      case 28 :
        bean.setCustom28( custom );
        break;
      case 29 :
        bean.setCustom29( custom );
        break;
      case 30 :
        bean.setCustom30( custom );
        break;
      } // switch ( idx )
    } // for ( int idx = 0 ; idx < arrCustoms.length ; idx++ )
    result = true;
    return result;
  }

}
