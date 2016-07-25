package com.beepcast.subscriber.view;

import java.util.List;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ListClientSubscriberViewService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ListClientSubscriberViewService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private ListClientSubscriberViewDAO dao;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ListClientSubscriberViewService() {
    dao = new ListClientSubscriberViewDAO();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ListClientSubscriberViewBean selectByClientIdPhoneNumber(
      int clientId , String phoneNumber , int top , int limit ) {

    return selectByClientIdSubscriberGroupIdPhoneNumber( clientId , 0 ,
        phoneNumber , top , limit );

  }

  public ListClientSubscriberViewBean selectByClientIdSubscriberGroupIdPhoneNumber(
      int clientId , int subscriberGroupId , String phoneNumber , int top ,
      int limit ) {

    ListClientSubscriberViewBean bean = ListClientSubscriberViewBeanFactory
        .createListClientSubscriberViewBean( clientId , subscriberGroupId ,
            phoneNumber );

    if ( clientId < 1 ) {
      return bean;
    }

    if ( subscriberGroupId < 1 ) {
      if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
        return bean;
      }
    }

    bean.setTotalBeans( dao.totalBeans( bean.getClientId() ,
        bean.getSubscriberGroupId() , bean.getPhoneNumber() ) );

    List listBeans = dao.listBeans( bean.getClientId() ,
        bean.getSubscriberGroupId() , bean.getPhoneNumber() , top , limit );

    if ( listBeans != null ) {
      bean.getListBeans().addAll( listBeans );
    }

    return bean;
  }
}
