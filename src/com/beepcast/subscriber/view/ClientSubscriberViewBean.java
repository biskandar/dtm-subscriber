package com.beepcast.subscriber.view;

import java.util.Date;

public class ClientSubscriberViewBean {

  private int clientSubscriberId;
  private int subscriberGroupId;
  private String subscriberGroupName;
  private String phoneNumber;
  private boolean subscribed;
  private Date dateSubscribed;
  private boolean globalSubscribed;
  private Date dateGlobalSubscribed;
  private boolean globalInvalid;
  private Date dateGlobalInvalid;
  private boolean globalDnc;
  private Date dateGlobalDnc;

  public ClientSubscriberViewBean() {
  }

  public int getClientSubscriberId() {
    return clientSubscriberId;
  }

  public void setClientSubscriberId( int clientSubscriberId ) {
    this.clientSubscriberId = clientSubscriberId;
  }

  public int getSubscriberGroupId() {
    return subscriberGroupId;
  }

  public void setSubscriberGroupId( int subscriberGroupId ) {
    this.subscriberGroupId = subscriberGroupId;
  }

  public String getSubscriberGroupName() {
    return subscriberGroupName;
  }

  public void setSubscriberGroupName( String subscriberGroupName ) {
    this.subscriberGroupName = subscriberGroupName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber( String phoneNumber ) {
    this.phoneNumber = phoneNumber;
  }

  public boolean isSubscribed() {
    return subscribed;
  }

  public void setSubscribed( boolean subscribed ) {
    this.subscribed = subscribed;
  }

  public Date getDateSubscribed() {
    return dateSubscribed;
  }

  public void setDateSubscribed( Date dateSubscribed ) {
    this.dateSubscribed = dateSubscribed;
  }

  public boolean isGlobalSubscribed() {
    return globalSubscribed;
  }

  public void setGlobalSubscribed( boolean globalSubscribed ) {
    this.globalSubscribed = globalSubscribed;
  }

  public Date getDateGlobalSubscribed() {
    return dateGlobalSubscribed;
  }

  public void setDateGlobalSubscribed( Date dateGlobalSubscribed ) {
    this.dateGlobalSubscribed = dateGlobalSubscribed;
  }

  public boolean isGlobalInvalid() {
    return globalInvalid;
  }

  public void setGlobalInvalid( boolean globalInvalid ) {
    this.globalInvalid = globalInvalid;
  }

  public Date getDateGlobalInvalid() {
    return dateGlobalInvalid;
  }

  public void setDateGlobalInvalid( Date dateGlobalInvalid ) {
    this.dateGlobalInvalid = dateGlobalInvalid;
  }

  public boolean isGlobalDnc() {
    return globalDnc;
  }

  public void setGlobalDnc( boolean globalDnc ) {
    this.globalDnc = globalDnc;
  }

  public Date getDateGlobalDnc() {
    return dateGlobalDnc;
  }

  public void setDateGlobalDnc( Date dateGlobalDnc ) {
    this.dateGlobalDnc = dateGlobalDnc;
  }

  @Override
  public String toString() {
    return "ClientSubscriberViewBean [clientSubscriberId=" + clientSubscriberId
        + ", subscriberGroupId=" + subscriberGroupId + ", subscriberGroupName="
        + subscriberGroupName + ", phoneNumber=" + phoneNumber
        + ", subscribed=" + subscribed + ", globalSubscribed="
        + globalSubscribed + ", globalInvalid=" + globalInvalid
        + ", globalDnc=" + globalDnc + "]";
  }

}
