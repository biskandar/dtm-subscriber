package com.beepcast.subscriber;

import java.util.List;

public interface SubscriberGroupApi {

  public SubscriberGroupBean getSubscriberGroupBean( int subscriberGroupId );

  public SubscriberGroupBean getSubscriberGroupBean( int clientId ,
      String groupName );

  public List listSubscriberGroupBeans( int clientId );

  public SubscriberGroupBean insertSubscriberGroup( int clientId ,
      String groupName );

  public boolean updateSubscriberGroup( int subscriberGroupId , String groupName );

  public boolean setInActiveSubscriberGroup( int subscriberGroupId );

  public int deleteNumbersInSubscriberGroup( int subscriberGroupId );

}
