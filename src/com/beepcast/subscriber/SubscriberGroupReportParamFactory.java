package com.beepcast.subscriber;

public class SubscriberGroupReportParamFactory {

  public static SubscriberGroupReportParamBean createSubscriberGroupReportParamBean() {
    return createSubscriberGroupReportParamBean( 0 , 0 , null , null , null ,
        true );
  }

  public static SubscriberGroupReportParamBean createSubscriberGroupReportParamBean(
      int subscriberGroupId , String field , String value ) {
    return createSubscriberGroupReportParamBean( 0 , subscriberGroupId , field ,
        value , null , true );
  }

  public static SubscriberGroupReportParamBean createSubscriberGroupReportParamBean(
      int id , int subscriberGroupId , String field , String value ,
      String description , boolean active ) {
    SubscriberGroupReportParamBean bean = new SubscriberGroupReportParamBean();
    bean.setId( id );
    bean.setSubscriberGroupId( subscriberGroupId );
    bean.setField( field );
    bean.setValue( value );
    bean.setDescription( description );
    bean.setActive( active );
    return bean;
  }
}
