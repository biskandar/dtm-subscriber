package com.beepcast.subscriber;

public class SubscriberGroupCommon {

  public static String headerLog( SubscriberGroupBean bean ) {
    String headerLog = "";
    if ( bean != null ) {
      headerLog = headerLog( bean.getId() );
    }
    return headerLog;
  }

  public static String headerLog( int subscriberGroupId ) {
    String headerLog = "";
    if ( subscriberGroupId > 0 ) {
      headerLog = "[List-" + subscriberGroupId + "] ";
    }
    return headerLog;
  }

}
