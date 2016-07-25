package com.beepcast.subscriber;

public interface ClientSubscriberApi {

  public int insertClientSubscriberBean( ClientSubscriberBean bean ,
      boolean allowDuplicatedNumbers , boolean allowOverridedNumbers );

  public ClientSubscriberBean getClientSubscriberBean( int clientSubscriberId );

  public ClientSubscriberBean getClientSubscriberBean( int clientId ,
      int subscriberGroupId , String phoneNumber );

  public boolean deleteClientSubscriberBean( int clientSubscriberId );

  public boolean doSubscribed( int clientId , int subscriberGroupId ,
      String phoneNumber , boolean subscribed , boolean duplicated ,
      int fromEventId , String customerReferenceId ,
      String customerReferenceCode , String[] arrCustoms );

  public boolean doUnsubscribed( int clientId , String phoneNumber );

  public boolean doValidated( int clientId , String phoneNumber , boolean valid );

  public boolean doValidated( int clientId , String phoneNumber ,
      boolean valid , int fromEventId );

  public boolean doDncRegistered( int clientId , String phoneNumber ,
      int synchNo );

  public boolean doRsvp( int clientId , int subscriberGroupId ,
      String phoneNumber , String rsvpStatus );

  public long getTotalSubscriber( int subscriberGroupId ,
      boolean allowDuplicated , boolean useDateSend );

  public long getTotalUnsubscriber( int clientId );

  public long getTotalInvalidSubscriber( int clientId );

  public long getTotalDncSubscriber( int clientId );

}
