package com.beepcast.subscriber;

import java.util.ArrayList;

public interface SubscriberFileApi {

  public String filePathWithoutExtensionForListSubscriber(
      int subscriberGroupId , Boolean subscribedStatus , String channel );

  public String filePathWithoutExtensionForListUnsubscriber();

  public String filePathWithoutExtensionForListInvalidNumber();

  public String filePathWithoutExtensionForListDncNumber();

  public boolean extractListSubscriberToFile( String filePathWithoutExtension ,
      int subscriberGroupId , Boolean subscribedStatus , ArrayList listAddFields );

  public boolean extractListSubscriberToFileRsvp(
      String filePathWithoutExtension , int subscriberGroupId ,
      ArrayList listAddFields );

  public boolean extractListUnsubscriberToFile(
      String filePathWithoutExtension , int clientId );

  public boolean extractListInvalidNumberToFile(
      String filePathWithoutExtension , int clientId );

  public boolean extractListDncNumberToFile( String filePathWithoutExtension ,
      int clientId );

}
