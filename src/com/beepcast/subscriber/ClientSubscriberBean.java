package com.beepcast.subscriber;

import java.util.Date;

public class ClientSubscriberBean {

  private int id;
  private int clientId;
  private int subscriberGroupId;
  private String phone;
  private int subscribed;
  private int subscribedEventId;
  private Date dateSubscribed;
  private int globalSubscribed;
  private Date dateGlobalSubscribed;
  private String customerReferenceId;
  private String customerReferenceCode;
  private Date dateSend;
  private String description;
  private int globalInvalid;
  private Date dateGlobalInvalid;
  private int globalDnc;
  private Date dateGlobalDnc;
  private int active;
  private Date dateInserted;

  private ClientSubscriberCustomBean csCustomBean;

  public ClientSubscriberBean() {
  }

  public int getId() {
    return id;
  }

  public void setId( int id ) {
    this.id = id;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone( String phone ) {
    this.phone = phone;
  }

  public int getSubscribed() {
    return subscribed;
  }

  public void setSubscribed( int subscribed ) {
    this.subscribed = subscribed;
  }

  public int getSubscribedEventId() {
    return subscribedEventId;
  }

  public void setSubscribedEventId( int subscribedEventId ) {
    this.subscribedEventId = subscribedEventId;
  }

  public Date getDateSubscribed() {
    return dateSubscribed;
  }

  public void setDateSubscribed( Date dateSubscribed ) {
    this.dateSubscribed = dateSubscribed;
  }

  public int getGlobalSubscribed() {
    return globalSubscribed;
  }

  public void setGlobalSubscribed( int globalSubscribed ) {
    this.globalSubscribed = globalSubscribed;
  }

  public Date getDateGlobalSubscribed() {
    return dateGlobalSubscribed;
  }

  public void setDateGlobalSubscribed( Date dateGlobalSubscribed ) {
    this.dateGlobalSubscribed = dateGlobalSubscribed;
  }

  public String getCustomerReferenceId() {
    return customerReferenceId;
  }

  public void setCustomerReferenceId( String customerReferenceId ) {
    this.customerReferenceId = customerReferenceId;
  }

  public String getCustomerReferenceCode() {
    return customerReferenceCode;
  }

  public void setCustomerReferenceCode( String customerReferenceCode ) {
    this.customerReferenceCode = customerReferenceCode;
  }

  public Date getDateSend() {
    return dateSend;
  }

  public void setDateSend( Date dateSend ) {
    this.dateSend = dateSend;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public int getGlobalInvalid() {
    return globalInvalid;
  }

  public void setGlobalInvalid( int globalInvalid ) {
    this.globalInvalid = globalInvalid;
  }

  public Date getDateGlobalInvalid() {
    return dateGlobalInvalid;
  }

  public void setDateGlobalInvalid( Date dateGlobalInvalid ) {
    this.dateGlobalInvalid = dateGlobalInvalid;
  }

  public int getGlobalDnc() {
    return globalDnc;
  }

  public void setGlobalDnc( int globalDnc ) {
    this.globalDnc = globalDnc;
  }

  public Date getDateGlobalDnc() {
    return dateGlobalDnc;
  }

  public void setDateGlobalDnc( Date dateGlobalDnc ) {
    this.dateGlobalDnc = dateGlobalDnc;
  }

  public int getActive() {
    return active;
  }

  public void setActive( int active ) {
    this.active = active;
  }

  public Date getDateInserted() {
    return dateInserted;
  }

  public void setDateInserted( Date dateInserted ) {
    this.dateInserted = dateInserted;
  }

  public ClientSubscriberCustomBean getCsCustomBean() {
    return csCustomBean;
  }

  public void setCsCustomBean( ClientSubscriberCustomBean csCustomBean ) {
    this.csCustomBean = csCustomBean;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "ClientSubscriberBean ( " + "id = " + this.id + TAB
        + "clientId = " + this.clientId + TAB + "subscriberGroupId = "
        + this.subscriberGroupId + TAB + "phone = " + this.phone + TAB
        + "subscribed = " + this.subscribed + TAB + "subscribedEventId = "
        + this.subscribedEventId + TAB + "globalSubscribed = "
        + this.globalSubscribed + TAB + "description = " + this.description
        + TAB + "globalInvalid = " + this.globalInvalid + TAB + "globalDnc = "
        + this.globalDnc + TAB + "active = " + this.active + TAB + " )";
    return retValue;
  }

}
