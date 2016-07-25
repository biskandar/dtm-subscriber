package com.beepcast.subscriber;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.beepcast.dbmanager.util.DateTimeFormat;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "ClientSubscriberService" );

  public static final String TAG_PREUNSUBS = "pre-unsubs";
  public static final String TAG_PREINVALID = "pre-invalid";

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private SubscriberConf subscriberConf;

  private ClientSubscriberDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberService( SubscriberApp subscriberApp ) {

    if ( subscriberApp == null ) {
      subscriberApp = SubscriberApp.getInstance();
    }
    if ( subscriberApp == null ) {
      DLog.warning( lctx , "Failed to initialized , found null subscriber app" );
      return;
    }

    this.subscriberApp = subscriberApp;
    this.subscriberConf = subscriberApp.getSubscriberConf();

    dao = new ClientSubscriberDAO();

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public int insertClientSubscriberBean( ClientSubscriberBean bean ,
      boolean allowDuplicatedNumbers , boolean allowOverridedNumbers ) {
    int result = SubscriberApp.INSERTCSBNRSLT_ERROR;

    if ( bean == null ) {
      DLog.warning( lctx , "Failed to insert client subscriber "
          + ", found null bean" );
      return result;
    }

    // validate must be params
    if ( bean.getClientId() < 1 ) {
      DLog.warning( lctx , "Failed to perform insert "
          + ( bean.getSubscribed() > 0 ? "" : "un" )
          + "subscribed number , found zero clientId" );
      return result;
    }
    if ( bean.getSubscriberGroupId() < 1 ) {
      DLog.warning( lctx , "Failed to perform insert "
          + ( bean.getSubscribed() > 0 ? "" : "un" )
          + "subscribed number , found zero subscriberGroupId" );
      return result;
    }
    if ( StringUtils.isBlank( bean.getPhone() ) ) {
      DLog.warning( lctx , "Failed to perform insert "
          + ( bean.getSubscribed() > 0 ? "" : "un" )
          + "subscribed number , found null phoneNumber" );
      return result;
    }

    // compose header log
    String headerLog = "[Client-" + bean.getClientId() + "] ";
    headerLog += "[List-" + bean.getSubscriberGroupId() + "] ";
    headerLog += "[Subscriber-" + bean.getPhone() + "] ";

    // verify is the subscriber has active status ?
    if ( bean.getActive() < 1 ) {
      // when found inactive status try to insert into client
      // subscriber table straight away as a log record
      if ( !dao.insertClientSubscriber( subscriberApp.isDebug() , bean ) ) {
        DLog.warning( lctx , headerLog + "Failed to insert inactive "
            + "client subscriber bean into the client subscriber table" );
        return result;
      }
      DLog.debug( lctx , headerLog + "Successfully inserted inactive "
          + "client subscriber bean into the client subscriber table" );
      result = SubscriberApp.INSERTCSBNRSLT_OK;
      return result;
    }

    // prepare for the selected client subscriber
    ClientSubscriberBean selectedCsBean = null;

    // when not allow to duplicate the number
    if ( !allowDuplicatedNumbers ) {
      // validate by selecting the existing number
      selectedCsBean = dao.getClientSubscriberBean( subscriberApp.isDebug() ,
          bean.getClientId() , bean.getSubscriberGroupId() , bean.getPhone() );
      // when found any existing record and not allow override the number
      if ( ( selectedCsBean != null ) && ( !allowOverridedNumbers ) ) {
        // reject this new record
        DLog.warning( lctx , headerLog + "Failed to insert client "
            + "subscriber , the phone number is already exist before" );
        result = SubscriberApp.INSERTCSBNRSLT_FAILED;
        return result;
      }
    }

    // only for insertion new record will populate the subscription profile
    // ( selectedCsBean == null ) into the client subscriber bean
    if ( selectedCsBean == null ) {

      // verify is the phone number valid previously ?
      ClientSubscriberInvalidBean clientSubscriberInvalidBean = getClientSubscriberInvalidService()
          .queryClientSubscriberInvalidBean( bean.getClientId() ,
              bean.getPhone() );
      if ( clientSubscriberInvalidBean != null ) {
        // set global invalid as true
        bean.setGlobalInvalid( 1 );
        // set date global invalid same with date from the invalid list
        Date dateGlobalInvalid = clientSubscriberInvalidBean.getDateInserted();
        if ( dateGlobalInvalid == null ) {
          dateGlobalInvalid = new Date();
        }
        bean.setDateGlobalInvalid( dateGlobalInvalid );
        DLog.debug( lctx , headerLog + "Found client subscriber phone number "
            + "is in the global invalid table , set global invalid as true" );
      } else {
        // user can change the global invalid status manually
        bean.setGlobalInvalid( 0 );
        bean.setDateGlobalInvalid( null );
      }

      // update active status related to global invalid number
      if ( bean.getGlobalInvalid() > 0 ) {
        DLog.debug( lctx , headerLog + "Found global invalid is true "
            + ", force to set active status as false" );
        bean.setActive( 0 );
      }

      // verify is the phone number dnc registered previously ?
      ClientSubscriberDncBean clientSubscriberDncBean = getClientSubscriberDncService()
          .selectByClientIdPhoneNumber( bean.getClientId() , bean.getPhone() );
      if ( clientSubscriberDncBean != null ) {
        // set global dnc as true
        bean.setGlobalDnc( 1 );
        // set date global dnc same with date from the dnc list
        Date dateGlobalDnc = clientSubscriberDncBean.getDateInserted();
        if ( dateGlobalDnc == null ) {
          dateGlobalDnc = new Date();
        }
        bean.setDateGlobalDnc( dateGlobalDnc );
        DLog.debug( lctx , headerLog + "Found client subscriber phone number "
            + "is in the global dnc table , set global dnc as true" );
      } else {
        // user can change the global dnc status manually
        bean.setGlobalDnc( 0 );
        bean.setDateGlobalDnc( null );
      }

      // verify is the phone number unsubscribed previously ?
      ClientSubscriberUnsubsBean clientSubscriberUnsubsBean = getClientSubscriberUnsubsService()
          .queryClientSubscriberUnsubsBean( bean.getClientId() ,
              bean.getPhone() );
      if ( clientSubscriberUnsubsBean != null ) {
        // set off in the global subscribed field
        bean.setGlobalSubscribed( 0 );
        // set date global unsubs same with date from the unsubs list
        Date dateGlobalSubscribed = clientSubscriberUnsubsBean
            .getDateInserted();
        if ( dateGlobalSubscribed == null ) {
          dateGlobalSubscribed = new Date();
        }
        bean.setDateGlobalSubscribed( dateGlobalSubscribed );
        DLog.debug( lctx , headerLog + "Found client subscriber phone number "
            + "is in the global unsubs table , set global subscribed as false" );
      } else {
        // user can not change the global subscribed status manually
        bean.setGlobalSubscribed( 1 );
        bean.setDateGlobalSubscribed( null );
      }

    } // if ( selectedCsBean == null )

    // when found the phone number has unsubscribed status
    // than will do the unsubs mechanism
    if ( bean.getSubscribed() < 1 ) {
      DLog.debug( lctx , headerLog + "Found upload subscriber "
          + "phone number has unsubscribed status" );
      // insert into the unsubs table
      if ( getClientSubscriberUnsubsService().insertNewPhone(
          bean.getClientId() , bean.getPhone() , 0 ) ) {
        DLog.debug( lctx , headerLog + "Successfully insert "
            + "subscriber phone number into the global unsubs table" );
        // do un-subscribed in the current list ( duplicated numbers )
        if ( dao.doUnsubscribed( subscriberApp.isDebug() , bean.getClientId() ,
            bean.getPhone() , bean.getSubscriberGroupId() , 0 ) ) {
          DLog.debug( lctx , headerLog + "Synchronized unsubscribed phone "
              + "number from the current list" );
        }
        // do un-subscribed in the other list
        if ( dao.doGlobalUnsubscribed( subscriberApp.isDebug() ,
            bean.getClientId() , bean.getPhone() , bean.getSubscriberGroupId() ) > 0 ) {
          DLog.debug( lctx , headerLog + "Synchronized unsubscribed phone "
              + "number from the other list" );
        }
      } else {
        DLog.warning( lctx , headerLog + "Failed to insert "
            + "subscriber phone number into the global unsubs table" );
      }
    } // if ( bean.getSubscribed() < 1 )

    // persist the client subscriber bean into table
    if ( selectedCsBean == null ) {

      // insert client subscriber bean into table
      if ( !dao.insertClientSubscriber( subscriberApp.isDebug() , bean ) ) {
        DLog.warning(
            lctx ,
            headerLog + "Failed to insert active "
                + "subscriber bean , with global subscriber status = "
                + bean.getGlobalSubscribed() + " and global invalid status = "
                + bean.getGlobalInvalid() + " into the client subscriber table" );
        result = SubscriberApp.INSERTCSBNRSLT_ERROR;
        return result;
      }

      // re-query the last inserted client subscriber bean
      selectedCsBean = dao.getClientSubscriberBean( subscriberApp.isDebug() ,
          bean.getClientId() , bean.getSubscriberGroupId() , bean.getPhone() );
      if ( selectedCsBean == null ) {
        DLog.warning(
            lctx ,
            headerLog + "Failed to insert active "
                + "subscriber bean , with global subscriber status = "
                + bean.getGlobalSubscribed() + " and global invalid status = "
                + bean.getGlobalInvalid() + " into the client subscriber table" );
        result = SubscriberApp.INSERTCSBNRSLT_ERROR;
        return result;
      }

      // log it
      DLog.debug(
          lctx ,
          headerLog + "Successfully inserted client subscriber "
              + "profile : custRefId = " + bean.getCustomerReferenceId()
              + " , custRefCode = " + bean.getCustomerReferenceCode()
              + " , dateSend = "
              + DateTimeFormat.convertToString( bean.getDateSend() )
              + " , description = " + bean.getDescription() );

      // re update the bean
      bean.setId( selectedCsBean.getId() );

    } else {

      // re update the bean
      bean.setId( selectedCsBean.getId() );

      // persist the profile fields if found any
      if ( dao.updateClientSubscriberProfile( subscriberApp.isDebug() , bean ,
          false ) ) {
        DLog.debug(
            lctx ,
            headerLog + "Successfully updated client "
                + "subsriber profile : custRefId = "
                + bean.getCustomerReferenceId() + " , custRefCode = "
                + bean.getCustomerReferenceCode() + " , dateSend = "
                + DateTimeFormat.convertToString( bean.getDateSend() )
                + " , description = " + bean.getDescription() );
      } else {
        DLog.warning( lctx , headerLog + "Failed to update client "
            + "subscriber profile" );
      }

    }

    // persist client subscriber custom bean into table if found any
    ClientSubscriberCustomBean csCustomBean = bean.getCsCustomBean();
    if ( csCustomBean != null ) {
      csCustomBean.setClientSubscriberId( selectedCsBean.getId() );
      if ( getClientSubscriberCustomService().persist( csCustomBean ) ) {
        DLog.debug(
            lctx ,
            headerLog + "Successfully persisted client "
                + "subscriber custom bean : csId = "
                + csCustomBean.getClientSubscriberId() + " , custom1 = "
                + csCustomBean.getCustom1() + " , custom2 = "
                + csCustomBean.getCustom2() + " , custom3 = "
                + csCustomBean.getCustom3() + " , custom4 = "
                + csCustomBean.getCustom4() + " , custom5 = "
                + csCustomBean.getCustom5() );
      } else {
        DLog.warning( lctx , headerLog + "Failed to insert client "
            + "subscriber custom bean" );
      }
    } // if ( csCustomBean != null )

    result = SubscriberApp.INSERTCSBNRSLT_OK;
    return result;
  }

  public ClientSubscriberBean getClientSubscriberBean( int clientSubscriberId ) {
    ClientSubscriberBean bean = null;
    if ( clientSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to get client subscriber bean "
          + ", found zero id" );
      return bean;
    }

    // select from client subscriber table

    bean = dao.getClientSubscriberBean( subscriberApp.isDebug() ,
        clientSubscriberId );
    if ( bean == null ) {
      return bean;
    }

    // select also from client subscriber custom table

    ClientSubscriberCustomBean csCustomBean = getClientSubscriberCustomService()
        .select( clientSubscriberId );
    if ( csCustomBean == null ) {
      return bean;
    }

    // update into the bean's custom fields

    bean.setCsCustomBean( csCustomBean );

    return bean;
  }

  public ClientSubscriberBean getClientSubscriberBean( int clientId ,
      int subscriberGroupId , String phoneNumber ) {
    ClientSubscriberBean bean = null;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to get client subscriber bean "
          + ", found zero id" );
      return bean;
    }
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to get client subscriber bean "
          + ", found zero subscriber group id" );
      return bean;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      DLog.warning( lctx , "Failed to get client subscriber bean "
          + ", found blank phone number" );
      return bean;
    }

    // select from client subscriber table

    bean = dao.getClientSubscriberBean( subscriberApp.isDebug() , clientId ,
        subscriberGroupId , phoneNumber );
    if ( bean == null ) {
      return bean;
    }

    // read the record key id

    int clientSubscriberId = bean.getId();
    if ( clientSubscriberId < 1 ) {
      return bean;
    }

    // select also from client subscriber custom table

    ClientSubscriberCustomBean csCustomBean = getClientSubscriberCustomService()
        .select( clientSubscriberId );
    if ( csCustomBean == null ) {
      return bean;
    }

    // update into the bean's custom fields

    bean.setCsCustomBean( csCustomBean );

    return bean;
  }

  public boolean deleteClientSubscriberBean( int clientSubscriberId ) {
    boolean deleted = false;
    if ( clientSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to delete client subscriber bean "
          + ", found zero client subscriber id" );
      return deleted;
    }
    deleted = dao.deleteClientSubscriberBean( subscriberApp.isDebug() ,
        clientSubscriberId );
    return deleted;
  }

  public boolean doSubscribed( int clientId , int subscriberGroupId ,
      String phoneNumber , boolean subscribed , boolean duplicated ,
      int fromEventId , String customerReferenceId ,
      String customerReferenceCode ) {
    boolean result = false;

    // validate must be params
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to perform " + ( subscribed ? "" : "un" )
          + "subscribed , found zero clientId" );
      return result;
    }
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to perform " + ( subscribed ? "" : "un" )
          + "subscribed , found zero subscriberGroupId" );
      return result;
    }
    if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform " + ( subscribed ? "" : "un" )
          + "subscribed , found null phoneNumber" );
      return result;
    }

    // clean additional params
    fromEventId = ( fromEventId < 0 ) ? 0 : fromEventId;
    customerReferenceId = ( customerReferenceId == null ) ? ""
        : customerReferenceId.trim();
    customerReferenceCode = ( customerReferenceCode == null ) ? ""
        : customerReferenceCode.trim();

    // compose header log
    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[List-" + subscriberGroupId + "] ";
    headerLog += "[Subscriber-" + phoneNumber + "] ";

    // when found subscribed status as subs
    if ( subscribed ) {

      ClientSubscriberBean csBean = ClientSubscriberFactory
          .createClientSubscriberBean( clientId , subscriberGroupId ,
              phoneNumber , customerReferenceId , customerReferenceCode );
      csBean.setSubscribed( 1 );
      csBean.setSubscribedEventId( fromEventId );
      csBean.setDateSubscribed( new Date() );

      if ( duplicated ) {
        // found to allow duplicated record , just force to insert
        result = dao.insertClientSubscriber( subscriberApp.isDebug() , csBean );
      } else {
        // not allow to do the duplicated record
        result = dao
            .doSubscribed( subscriberApp.isDebug() , csBean.getClientId() ,
                csBean.getPhone() , csBean.getSubscriberGroupId() ,
                csBean.getSubscribedEventId() ,
                csBean.getCustomerReferenceId() ,
                csBean.getCustomerReferenceCode() );
        if ( !result ) {
          // failed to do the updated , assume is empty before
          // will do to insert as new record
          result = dao
              .insertClientSubscriber( subscriberApp.isDebug() , csBean );
        }
      }

      if ( result ) {
        DLog.debug( lctx , headerLog + "Successfully updated active "
            + "subscribed status field in the client subscriber table " );
        // clean in the unsubs table
        if ( getClientSubscriberUnsubsService().deletePhone( clientId ,
            phoneNumber ) ) {
          DLog.debug( lctx , headerLog + "Subscriber is cleaned "
              + "from the global unsubs table" );
        }
      }

      if ( result ) {
        DLog.debug( lctx , headerLog + "Successfully performed subscribe" );
      } else {
        DLog.warning( lctx , headerLog + "Failed to perform subscribe" );
      }

      return result;
    }

    // when found subscribed status as unsubs
    result = dao.doUnsubscribed( subscriberApp.isDebug() , clientId ,
        phoneNumber , subscriberGroupId , fromEventId );
    if ( !result ) {
      // assume the number is not in the list , try to insert into
      // the list still with unsubscribed flag
      DLog.debug( lctx , headerLog + "Failed to perform unsubscribed number "
          + "from the list , persist the number into the list with "
          + "unsubscribed enable" );
      ClientSubscriberBean csBean = ClientSubscriberFactory
          .createClientSubscriberBean( clientId , subscriberGroupId ,
              phoneNumber , customerReferenceId , customerReferenceCode );
      csBean.setSubscribed( 0 );
      csBean.setSubscribedEventId( fromEventId );
      csBean.setDateSubscribed( new Date() );
      result = dao.insertClientSubscriber( subscriberApp.isDebug() , csBean );
    }
    if ( result ) {
      DLog.debug( lctx , headerLog + "Successfully updated inactive "
          + "subscribed status field in the client subscriber table " );
      // insert into the unsubs table
      if ( getClientSubscriberUnsubsService().insertNewPhone( clientId ,
          phoneNumber , fromEventId ) ) {
        DLog.debug( lctx , headerLog + "Successfully insert subscriber "
            + "into the global unsubs table" );
        // unsubscribed in the another list
        int totalLists = dao.doGlobalUnsubscribed( subscriberApp.isDebug() ,
            clientId , phoneNumber , subscriberGroupId );
        DLog.debug( lctx , headerLog + "Synchronized phone number "
            + "from other list , effected = " + totalLists + " list(s)" );
      } else {
        DLog.warning( lctx , headerLog + "Failed to insert subcriber "
            + "into the global unsubs table" );
      }
    }
    if ( result ) {
      DLog.debug( lctx , headerLog + "Successfully perform unsubscribed" );
    } else {
      DLog.warning( lctx , headerLog + "Failed to perform unsubscribed" );
    }

    return result;
  }

  public boolean doUnsubscribed( int clientId , String phoneNumber ) {
    return doUnsubscribed( clientId , phoneNumber , 0 );
  }

  public boolean doUnsubscribed( int clientId , String phoneNumber ,
      int fromEventId ) {
    boolean result = false;

    // validate must be params
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to perform unsubscribed "
          + ", found zero clientId" );
      return result;
    }
    if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform unsubscribed "
          + ", found null phoneNumber" );
      return result;
    }

    // compose header log
    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[Subscriber-" + phoneNumber + "] ";

    // insert into the unsubs table
    if ( !getClientSubscriberUnsubsService().insertNewPhone( clientId ,
        phoneNumber , fromEventId ) ) {
      DLog.warning( lctx , headerLog + "Failed to perform unsubscribed "
          + ", found failed to insert subscriber number into "
          + "global unsubs table" );
      return result;
    }
    DLog.debug( lctx , headerLog + "Inserted the number into "
        + "global unsubs table , with : fromEventId = " + fromEventId );

    // unsubscribed in all particular number in the list
    int totalLists = dao.doGlobalUnsubscribed( subscriberApp.isDebug() ,
        clientId , phoneNumber , 0 );

    // log it
    DLog.debug( lctx , headerLog + "Synchronized unsubscribed phone number "
        + "from all the lists , total effected = " + totalLists + " list(s)" );

    result = true;
    return result;
  }

  public boolean doValidated( int clientId , String phoneNumber ,
      boolean valid , int fromEventId ) {
    boolean result = false;

    // validate must be params
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to perform update " + ( valid ? "" : "in" )
          + "valid number , found zero clientId" );
      return result;
    }
    if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform update " + ( valid ? "" : "in" )
          + "valid number , found null phoneNumber" );
      return result;
    }

    // compose header log
    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[MSISDN-" + phoneNumber + "] ";

    int totalRecords = 0;

    if ( valid ) {
      // make the phone number became valid
      totalRecords = dao.doGlobalInvalid( subscriberApp.isDebug() , clientId ,
          phoneNumber , false );
      DLog.debug( lctx , headerLog + "Updated client subscriber "
          + "global invalid flag set to false , effected = " + totalRecords
          + " record(s)" );
      if ( !getClientSubscriberInvalidService().deletePhone( clientId ,
          phoneNumber ) ) {
        DLog.warning( lctx , headerLog + "Failed to perform update "
            + ( valid ? "" : "in" ) + "valid number , found can not "
            + "delete phone number from the global invalid list table" );
        return result;
      }
    } else {
      // make the phone number became invalid
      totalRecords = dao.doGlobalInvalid( subscriberApp.isDebug() , clientId ,
          phoneNumber , true );
      DLog.debug( lctx , headerLog + "Updated client subscriber "
          + "global invalid flag set to true , effected = " + totalRecords
          + " record(s)" );
      if ( !getClientSubscriberInvalidService().insertNewPhone( clientId ,
          phoneNumber , fromEventId ) ) {
        DLog.warning( lctx , headerLog + "Failed to perform update "
            + ( valid ? "" : "in" ) + "valid number , found can not "
            + "insert phone number into the global invalid list table" );
        return result;
      }
    }

    DLog.warning( lctx , headerLog + "Successfully perform updated "
        + ( valid ? "" : "in" ) + "valid number" );

    result = true;
    return result;
  }

  public boolean doDncRegistered( int clientId , String phoneNumber ,
      int synchNo ) {
    boolean result = false;

    // validate must be params

    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to perform update dnc "
          + "register number , found zero clientId" );
      return result;
    }
    if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform update dnc "
          + "register number , found blank phoneNumber" );
      return result;
    }

    // compose header log

    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[MSISDN-" + phoneNumber + "] ";
    headerLog += "[SynchNo-" + synchNo + "] ";

    // update dnc register on the contact list

    int totalRecords = dao.doGlobalDnc( subscriberApp.isDebug() , clientId ,
        phoneNumber , true );
    DLog.debug( lctx , headerLog + "Updated client subscriber "
        + "global dnc flag set to true , effected = " + totalRecords
        + " record(s)" );

    // update dnc register on the global list

    if ( !getClientSubscriberDncService().insert(
        ClientSubscriberDncBeanFactory.createClientSubscriberDncBean( clientId ,
            phoneNumber , synchNo , null ) ) ) {
      DLog.warning( lctx , headerLog + "Failed to perform update "
          + "dnc register number , found can not insert phone number "
          + "into the global dnc list table" );
      return result;
    }

    // log it

    DLog.warning( lctx , headerLog + "Successfully perform updated dnc "
        + "register number" );

    result = true;
    return result;
  }

  public boolean cleanDncUnregistered( int clientId , int synchNoPrev ,
      int synchNoNext , long sleep ) {
    boolean result = false;
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to clean dnc unregistered "
          + ", found zero clientId" );
      return result;
    }
    if ( synchNoPrev < 0 ) {
      DLog.warning( lctx , "Failed to clean dnc unregistered "
          + ", found invalid synchNoPrev" );
      return result;
    }
    if ( synchNoNext < 0 ) {
      DLog.warning( lctx , "Failed to clean dnc unregistered "
          + ", found invalid synchNoNext" );
      return result;
    }
    try {
      Thread thread = new ClientSubscriberDncCleaner( clientId , synchNoPrev ,
          synchNoNext , sleep );
      thread.start();
      result = true;
    } catch ( Exception e ) {
      DLog.warning( lctx , "Failed to clean dnc unregistered , " + e );
    }
    return result;
  }

  public boolean doRsvp( int clientId , int subscriberGroupId ,
      String phoneNumber , String rsvpStatus ) {
    boolean result = false;

    // validate must be params
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to perform rsvp " + rsvpStatus
          + " , found zero clientId" );
      return result;
    }
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to perform rsvp " + rsvpStatus
          + " , found zero subscriberGroupId" );
      return result;
    }
    if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform rsvp " + rsvpStatus
          + " , found null phoneNumber" );
      return result;
    }

    // clean params
    if ( rsvpStatus == null ) {
      rsvpStatus = RsvpStatus.BLANK;
    }

    // compose header log
    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[List-" + subscriberGroupId + "] ";
    headerLog += "[Subscriber-" + phoneNumber + "] ";

    // update rsvp parameters
    if ( !dao.updateRsvpProfile( subscriberApp.isDebug() , clientId ,
        subscriberGroupId , phoneNumber , rsvpStatus ) ) {
      DLog.warning( lctx , "Failed to perform rsvp " + rsvpStatus
          + " , failed to update inside table" );
      return result;
    }

    result = true;
    return result;
  }

  public long getTotalSubscriber( int subscriberGroupId ,
      boolean allowDuplicated , boolean useDateSend ) {
    long total = 0;
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to get total subscriber "
          + ", found zero subscriberGroupId" );
      return total;
    }
    total = dao.getTotalSubscriber( subscriberGroupId , allowDuplicated ,
        useDateSend );
    return total;
  }

  public boolean isExistClientSubscriber( ClientSubscriberBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to insert client subscriber "
          + ", found empty ClientSubscriberBean" );
      return result;
    }
    result = dao.isExistClientSubscriber( bean );
    return result;
  }

  public boolean updateClientSubscriberProfile( ClientSubscriberBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      DLog.warning( lctx , "Failed to update profile "
          + ", found empty ClientSubscriberBean" );
      return result;
    }
    if ( bean.getId() < 1 ) {
      DLog.warning( lctx , "Failed to update profile "
          + ", found blank ClientSubscriberBean Id" );
      return result;
    }
    if ( !dao.updateClientSubscriberProfile( subscriberApp.isDebug() , bean ,
        true ) ) {
      DLog.warning( lctx , "Failed to update profile "
          + ", found invalid ClientSubscriberBean Id" );
      return result;
    }
    getClientSubscriberCustomService().persist( bean.getCsCustomBean() );
    result = true;
    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberCustomService getClientSubscriberCustomService() {
    return subscriberApp.getClientSubscriberCustomService();
  }

  public ClientSubscriberUnsubsService getClientSubscriberUnsubsService() {
    return subscriberApp.getClientSubscriberUnsubsService();
  }

  public ClientSubscriberInvalidService getClientSubscriberInvalidService() {
    return subscriberApp.getClientSubscriberInvalidService();
  }

  public ClientSubscriberDncService getClientSubscriberDncService() {
    return subscriberApp.getClientSubscriberDncService();
  }

}
