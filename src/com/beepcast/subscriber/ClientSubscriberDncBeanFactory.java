package com.beepcast.subscriber;

public class ClientSubscriberDncBeanFactory {

  public static ClientSubscriberDncBean createClientSubscriberDncBean(
      int clientId , String phone , int synchNo , String type ) {
    ClientSubscriberDncBean bean = new ClientSubscriberDncBean();
    bean.setClientId( clientId );
    bean.setPhone( phone );
    bean.setSynchNo( synchNo );
    bean.setType( type );
    bean.setActive( true );
    bean.setDescription( "" );
    return bean;
  }

}
