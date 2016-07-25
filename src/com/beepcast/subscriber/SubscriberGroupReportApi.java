package com.beepcast.subscriber;

public interface SubscriberGroupReportApi {

  public int totalAllNumbers( int subscriberGroupId );

  public int totalSubscribedNumbers( int subscriberGroupId );

  public int totalUniqueNumbers( int subscriberGroupId );

  public int totalSubscribedUniqueNumbers( int subscriberGroupId );

  public int totalAllScheduled( int subscriberGroupId );

  public int totalSubscribedScheduled( int subscriberGroupId );

  public int totalUniqueScheduled( int subscriberGroupId );

  public int totalSubscribedUniqueScheduled( int subscriberGroupId );

}
