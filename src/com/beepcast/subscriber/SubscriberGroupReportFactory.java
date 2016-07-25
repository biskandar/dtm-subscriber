package com.beepcast.subscriber;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupReportFactory {

  static final DLogContext lctx = new SimpleContext(
      "SubscriberGroupReportFactory" );

  public static SubscriberGroupReportBean createSubscriberGroupReportBean(
      int subscriberGroupId ) {
    SubscriberGroupReportBean bean = new SubscriberGroupReportBean();

    bean.setId( 0 );
    bean.setSubscriberGroupId( subscriberGroupId );
    bean.setTotalUploaded( 0 );
    bean.setTotalSubscribed( 0 );
    bean.setTotalDuplicatedNumber( 0 );
    bean.setTotalInvalidNumber( 0 );
    bean.setActive( true );

    return bean;
  }

}
