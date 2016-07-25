package com.beepcast.subscriber;

public class SubscriberGroupReportCommon {

  public static String headerLog( SubscriberGroupReportBean bean ) {
    String headerLog = "";
    if ( bean != null ) {
      headerLog = headerLog( bean.getSubscriberGroupId() );
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
