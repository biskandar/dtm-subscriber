package com.beepcast.subscriber.view;

public class ListClientSubscriberViewBeanFactory {

  public static ListClientSubscriberViewBean createListClientSubscriberViewBean(
      int clientId , int subscriberGroupId , String phoneNumber ) {
    ListClientSubscriberViewBean bean = new ListClientSubscriberViewBean();
    bean.setClientId( clientId );
    bean.setSubscriberGroupId( subscriberGroupId );
    bean.setPhoneNumber( phoneNumber );
    bean.setTotalBeans( 0 );
    bean.getListBeans().clear();
    return bean;
  }

}
