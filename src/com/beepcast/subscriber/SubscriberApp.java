package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beepcast.subscriber.view.ClientSubscriberViewService;
import com.beepcast.subscriber.view.GroupSubscriberViewService;
import com.beepcast.subscriber.view.ListGroupSubscriberViewService;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberApp implements Module , SubscriberFileApi ,
    ClientSubscriberApi , SubscriberGroupApi , SubscriberGroupReportApi {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberApp" );

  public static final int INSERTCSBNRSLT_OK = 0;
  public static final int INSERTCSBNRSLT_ERROR = 1;
  public static final int INSERTCSBNRSLT_FAILED = 2;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberConf subscriberConf;

  private ClientSubscriberService clientSubscriberService;
  private ClientSubscriberCustomService clientSubscriberCustomService;
  private ClientSubscriberUnsubsService clientSubscriberUnsubsService;
  private ClientSubscriberInvalidService clientSubscriberInvalidService;
  private ClientSubscriberDncService clientSubscriberDncService;

  private ClientSubscriberFileService clientSubscriberFileService;
  private ClientSubscriberFileRsvpService clientSubscriberFileRsvpService;
  private ClientSubscriberUnsubsFileService clientSubscriberUnsubsFileService;
  private ClientSubscriberInvalidFileService clientSubscriberInvalidFileService;
  private ClientSubscriberDncFileService clientSubscriberDncFileService;

  private SubscriberGroupService subscriberGroupService;

  private ListGroupSubscriberViewService listGroupSubscriberViewService;
  private GroupSubscriberViewService groupSubscriberViewService;
  private SubscriberGroupReportService subscriberGroupReportService;
  private ClientSubscriberViewService clientSubscriberViewService;

  private SubscriberManagement subscriberManagement;

  private boolean initialized;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void init( SubscriberConf subscriberConf ) {
    initialized = false;

    if ( subscriberConf == null ) {
      DLog.warning( lctx , "Failed to initialized subscriber app "
          + ", found null conf" );
      return;
    }

    this.subscriberConf = subscriberConf;
    DLog.debug( lctx , "Use " + subscriberConf );

    // log conf
    DLog.debug( lctx , "Debug mode = " + subscriberConf.isDebug() );
    DLog.debug( lctx , "Read synchSubscriberGroupReportConf.enabled = "
        + subscriberConf.getSynchSubscriberGroupReportConf().isEnabled() );
    DLog.debug( lctx , "Read cleanClientSubscriberInvalidRecordConf.enabled = "
        + subscriberConf.getCleanClientSubscriberInvalidRecordConf()
            .isEnabled() );

    // create service object
    clientSubscriberService = new ClientSubscriberService( this );
    clientSubscriberCustomService = new ClientSubscriberCustomService( this );
    clientSubscriberUnsubsService = new ClientSubscriberUnsubsService( this );
    clientSubscriberInvalidService = new ClientSubscriberInvalidService( this );
    clientSubscriberDncService = new ClientSubscriberDncService( this );

    clientSubscriberFileService = new ClientSubscriberFileService( this );
    clientSubscriberFileRsvpService = new ClientSubscriberFileRsvpService( this );
    clientSubscriberUnsubsFileService = new ClientSubscriberUnsubsFileService(
        this );
    clientSubscriberInvalidFileService = new ClientSubscriberInvalidFileService(
        this );
    clientSubscriberDncFileService = new ClientSubscriberDncFileService( this );

    subscriberGroupService = new SubscriberGroupService( this );

    listGroupSubscriberViewService = new ListGroupSubscriberViewService( this );
    groupSubscriberViewService = new GroupSubscriberViewService( this );
    subscriberGroupReportService = new SubscriberGroupReportService( this );
    clientSubscriberViewService = new ClientSubscriberViewService( this );

    subscriberManagement = new SubscriberManagement( this );

    initialized = true;
    DLog.debug( lctx , "all module(s) are initialized" );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void moduleStart() {
    if ( !initialized ) {
      DLog.error( lctx , "Failed to start subscriber module "
          + ", found not yet initialized" );
      return;
    }

    // start management
    subscriberManagement.moduleStart();

    DLog.debug( lctx , "all module(s) are started" );
  }

  public void moduleStop() {
    if ( !initialized ) {
      DLog.error( lctx , "Failed to stop subscriber module "
          + ", found not yet initialized" );
      return;
    }

    // stop management
    subscriberManagement.moduleStop();

    DLog.debug( lctx , "all module(s) are stopped" );
  }

  public String filePathWithoutExtensionForListSubscriber(
      int subscriberGroupId , Boolean subscribedStatus , String channel ) {
    return clientSubscriberFileService.filePathWithoutExtension(
        subscriberGroupId , subscribedStatus , channel );
  }

  public String filePathWithoutExtensionForListUnsubscriber() {
    return clientSubscriberUnsubsFileService.filePathWithoutExtension();
  }

  public String filePathWithoutExtensionForListInvalidNumber() {
    return clientSubscriberInvalidFileService.filePathWithoutExtension();
  }

  public String filePathWithoutExtensionForListDncNumber() {
    return clientSubscriberDncFileService.filePathWithoutExtension();
  }

  public boolean extractListSubscriberToFile( String filePathWithoutExtension ,
      int subscriberGroupId , Boolean subscribedStatus , ArrayList listAddFields ) {
    return clientSubscriberFileService.generateListOfAllNumbers(
        filePathWithoutExtension , subscriberGroupId , subscribedStatus ,
        listAddFields );
  }

  public boolean extractListSubscriberToFileRsvp(
      String filePathWithoutExtension , int subscriberGroupId ,
      ArrayList listAddFields ) {
    return clientSubscriberFileRsvpService.generateListOfAllNumbers(
        filePathWithoutExtension , subscriberGroupId , listAddFields );
  }

  public boolean extractListUnsubscriberToFile(
      String filePathWithoutExtension , int clientId ) {
    return clientSubscriberUnsubsFileService.generateListOfAllNumbers(
        filePathWithoutExtension , clientId );
  }

  public boolean extractListInvalidNumberToFile(
      String filePathWithoutExtension , int clientId ) {
    return clientSubscriberInvalidFileService.generateListOfAllNumbers(
        filePathWithoutExtension , clientId );
  }

  public boolean extractListDncNumberToFile( String filePathWithoutExtension ,
      int clientId ) {
    return clientSubscriberDncFileService.generateListOfAllNumbers(
        filePathWithoutExtension , clientId );
  }

  public int insertClientSubscriberBean( ClientSubscriberBean bean ,
      boolean allowDuplicatedNumbers , boolean allowOverridedNumbers ) {
    return clientSubscriberService.insertClientSubscriberBean( bean ,
        allowDuplicatedNumbers , allowOverridedNumbers );
  }

  public ClientSubscriberBean getClientSubscriberBean( int clientSubscriberId ) {
    return clientSubscriberService.getClientSubscriberBean( clientSubscriberId );
  }

  public ClientSubscriberBean getClientSubscriberBean( int clientId ,
      int subscriberGroupId , String phoneNumber ) {
    return clientSubscriberService.getClientSubscriberBean( clientId ,
        subscriberGroupId , phoneNumber );
  }

  public boolean deleteClientSubscriberBean( int clientSubscriberId ) {
    return clientSubscriberService
        .deleteClientSubscriberBean( clientSubscriberId );
  }

  public boolean doSubscribed( int clientId , int subscriberGroupId ,
      String phoneNumber , boolean subscribed , boolean duplicated ,
      int fromEventId , String customerReferenceId ,
      String customerReferenceCode , String[] arrCustoms ) {
    boolean result = false;

    // first , try to subscribe the phone number
    if ( !clientSubscriberService.doSubscribed( clientId , subscriberGroupId ,
        phoneNumber , subscribed , duplicated , fromEventId ,
        customerReferenceId , customerReferenceCode ) ) {
      return result;
    }

    // set result as true
    result = true;

    // when found no custom fields to update , just bypass
    if ( ( arrCustoms == null ) || ( arrCustoms.length < 1 ) ) {
      return result;
    }

    // to persist all the custom fields , require to select the latest
    // valid record
    ClientSubscriberBean csBean = clientSubscriberService
        .getClientSubscriberBean( clientId , subscriberGroupId , phoneNumber );
    if ( csBean == null ) {
      DLog.warning( lctx , "Failed to update array custom fields , found "
          + "invalid number and/or subscriber group id and/or client id" );
      return result;
    }

    // log it
    DLog.debug( lctx ,
        "Successfully performed subscribe with : id = " + csBean.getId()
            + " , clientId = " + clientId + " , subscriberGroupId = "
            + subscriberGroupId + " , phoneNumber = " + phoneNumber );

    // get custom bean profile from current bean if found any
    ClientSubscriberCustomBean cscBean = csBean.getCsCustomBean();

    // persist new custom fields
    DLog.debug( lctx ,
        "Persist with the new costum fields : " + Arrays.asList( arrCustoms ) );
    if ( cscBean == null ) {
      cscBean = ClientSubscriberFactory.createClientSubscriberCustomBean(
          csBean.getId() , arrCustoms );
      if ( clientSubscriberCustomService.insert( cscBean ) ) {
        DLog.debug( lctx , "Successfully inserted new custom bean" );
      } else {
        DLog.warning( lctx , "Failed to insert new custom bean" );
      }
    } else {
      ClientSubscriberFactory.updateCustoms( arrCustoms , cscBean );
      if ( clientSubscriberCustomService.update( cscBean ) ) {
        DLog.debug( lctx , "Successfully updated new custom bean" );
      } else {
        DLog.warning( lctx , "Failed to update new custom bean" );
      }
    }

    return result;
  }

  public boolean doUnsubscribed( int clientId , String phoneNumber ) {
    return clientSubscriberService.doUnsubscribed( clientId , phoneNumber );
  }

  public boolean doValidated( int clientId , String phoneNumber , boolean valid ) {
    return clientSubscriberService.doValidated( clientId , phoneNumber , valid ,
        0 );
  }

  public boolean doValidated( int clientId , String phoneNumber ,
      boolean valid , int fromEventId ) {
    return clientSubscriberService.doValidated( clientId , phoneNumber , valid ,
        fromEventId );
  }

  public boolean doDncRegistered( int clientId , String phoneNumber ,
      int synchNo ) {
    return clientSubscriberService.doDncRegistered( clientId , phoneNumber ,
        synchNo );
  }

  public boolean doRsvp( int clientId , int subscriberGroupId ,
      String phoneNumber , String rsvpStatus ) {
    return clientSubscriberService.doRsvp( clientId , subscriberGroupId ,
        phoneNumber , rsvpStatus );
  }

  public boolean doRsvpBlank( int clientId , int subscriberGroupId ,
      String phoneNumber ) {
    return clientSubscriberService.doRsvp( clientId , subscriberGroupId ,
        phoneNumber , RsvpStatus.BLANK );
  }

  public boolean doRsvpYes( int clientId , int subscriberGroupId ,
      String phoneNumber ) {
    return clientSubscriberService.doRsvp( clientId , subscriberGroupId ,
        phoneNumber , RsvpStatus.YES );
  }

  public boolean doRsvpNo( int clientId , int subscriberGroupId ,
      String phoneNumber ) {
    return clientSubscriberService.doRsvp( clientId , subscriberGroupId ,
        phoneNumber , RsvpStatus.NO );
  }

  public long getTotalSubscriber( int subscriberGroupId ,
      boolean allowDuplicated , boolean useDateSend ) {
    return clientSubscriberService.getTotalSubscriber( subscriberGroupId ,
        allowDuplicated , useDateSend );
  }

  public long getTotalUnsubscriber( int clientId ) {
    return clientSubscriberUnsubsService.totalActiveNumbers( clientId );
  }

  public long getTotalInvalidSubscriber( int clientId ) {
    return clientSubscriberInvalidService.totalActiveNumbers( clientId );
  }

  public long getTotalDncSubscriber( int clientId ) {
    return clientSubscriberDncService.selectTotalByClientIdActive( clientId ,
        true );
  }

  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupBean getSubscriberGroupBean( int subscriberGroupId ) {
    return subscriberGroupService.getSubscriberGroupBean( subscriberGroupId );
  }

  public SubscriberGroupBean getSubscriberGroupBean( int clientId ,
      String groupName ) {
    return subscriberGroupService.getSubscriberGroupBean( clientId , groupName );
  }

  public List listSubscriberGroupBeans( int clientId ) {
    return subscriberGroupService.listSubscriberGroupBeans( clientId );
  }

  public SubscriberGroupBean insertSubscriberGroup( int clientId ,
      String groupName ) {
    String headerLog = "[Client-" + clientId + "] ";
    headerLog += "[SubscriberGroup-" + groupName + "] ";
    SubscriberGroupBean bean = subscriberGroupService.insertSubscriberGroup(
        clientId , groupName );
    if ( bean != null ) {
      DLog.debug( lctx , headerLog + "Successfully inserted "
          + "a subscriber group into table" );
    } else {
      DLog.warning( lctx , headerLog + "Failed to insert "
          + "a subscriber group into table" );
    }
    return bean;
  }

  public boolean updateSubscriberGroup( int subscriberGroupId , String groupName ) {
    return subscriberGroupService.updateSubscriberGroup( subscriberGroupId ,
        groupName );
  }

  public boolean setInActiveSubscriberGroup( int subscriberGroupId ) {
    return subscriberGroupService
        .setInActiveSubscriberGroup( subscriberGroupId );
  }

  public int deleteNumbersInSubscriberGroup( int subscriberGroupId ) {
    return subscriberGroupService
        .deleteNumbersInSubscriberGroup( subscriberGroupId );
  }

  // ////////////////////////////////////////////////////////////////////////////

  public int totalAllNumbers( int subscriberGroupId ) {
    return subscriberGroupReportService.totalAllNumbers( subscriberGroupId );
  }

  public int totalSubscribedNumbers( int subscriberGroupId ) {
    return subscriberGroupReportService
        .totalSubscribedNumbers( subscriberGroupId );
  }

  public int totalUniqueNumbers( int subscriberGroupId ) {
    return subscriberGroupReportService.totalUniqueNumbers( subscriberGroupId );
  }

  public int totalSubscribedUniqueNumbers( int subscriberGroupId ) {
    return subscriberGroupReportService
        .totalSubscribedUniqueNumbers( subscriberGroupId );
  }

  public int totalAllScheduled( int subscriberGroupId ) {
    return subscriberGroupReportService.totalAllScheduled( subscriberGroupId );
  }

  public int totalSubscribedScheduled( int subscriberGroupId ) {
    return subscriberGroupReportService
        .totalSubscribedScheduled( subscriberGroupId );
  }

  public int totalUniqueScheduled( int subscriberGroupId ) {
    return subscriberGroupReportService
        .totalUniqueScheduled( subscriberGroupId );
  }

  public int totalSubscribedUniqueScheduled( int subscriberGroupId ) {
    return subscriberGroupReportService
        .totalSubscribedUniqueScheduled( subscriberGroupId );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberConf getSubscriberConf() {
    return subscriberConf;
  }

  public boolean isDebug() {
    return subscriberConf.isDebug();
  }

  public ClientSubscriberService getClientSubscriberService() {
    return clientSubscriberService;
  }

  public ClientSubscriberCustomService getClientSubscriberCustomService() {
    return clientSubscriberCustomService;
  }

  public ClientSubscriberUnsubsService getClientSubscriberUnsubsService() {
    return clientSubscriberUnsubsService;
  }

  public ClientSubscriberInvalidService getClientSubscriberInvalidService() {
    return clientSubscriberInvalidService;
  }

  public ClientSubscriberDncService getClientSubscriberDncService() {
    return clientSubscriberDncService;
  }

  public SubscriberGroupService getSubscriberGroupService() {
    return subscriberGroupService;
  }

  public ListGroupSubscriberViewService getListGroupSubscriberViewService() {
    return listGroupSubscriberViewService;
  }

  public GroupSubscriberViewService getGroupSubscriberViewService() {
    return groupSubscriberViewService;
  }

  public SubscriberGroupReportService getSubscriberGroupReportService() {
    return subscriberGroupReportService;
  }

  public ClientSubscriberViewService getClientSubscriberViewService() {
    return clientSubscriberViewService;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private void init() {
    // nothing to do
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Singleton Pattern
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static final SubscriberApp INSTANCE = new SubscriberApp();

  private SubscriberApp() {
    init();
  }

  public static final SubscriberApp getInstance() {
    return INSTANCE;
  }

}
