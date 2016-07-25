package com.beepcast.subscriber.view;

import java.util.ArrayList;
import java.util.List;

public class ListClientSubscriberViewBean {

  private int clientId;
  private int subscriberGroupId;
  private String phoneNumber;
  private int totalBeans;
  private List listBeans;

  public ListClientSubscriberViewBean() {
    listBeans = new ArrayList();
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId( int clientId ) {
    this.clientId = clientId;
  }

  public int getSubscriberGroupId() {
    return subscriberGroupId;
  }

  public void setSubscriberGroupId( int subscriberGroupId ) {
    this.subscriberGroupId = subscriberGroupId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber( String phoneNumber ) {
    this.phoneNumber = phoneNumber;
  }

  public int getTotalBeans() {
    return totalBeans;
  }

  public void setTotalBeans( int totalBeans ) {
    this.totalBeans = totalBeans;
  }

  public List getListBeans() {
    return listBeans;
  }

}
