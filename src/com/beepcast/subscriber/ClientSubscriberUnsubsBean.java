package com.beepcast.subscriber;

import java.util.Date;

import com.beepcast.dbmanager.util.DateTimeFormat;

public class ClientSubscriberUnsubsBean {

  private int id;
  private int clientId;
  private String phone;
  private int fromEventId;
  private int active;
  private Date dateInserted;
  private Date dateUpdated;

  public ClientSubscriberUnsubsBean() {
    active = 1;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone( String phone ) {
    this.phone = phone;
  }

  public int getFromEventId() {
    return fromEventId;
  }

  public void setFromEventId( int fromEventId ) {
    this.fromEventId = fromEventId;
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

  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated( Date dateUpdated ) {
    this.dateUpdated = dateUpdated;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "ClientSubscriberUnsubsBean ( " + "id = " + this.id + TAB
        + "clientId = " + this.clientId + TAB + "phone = " + this.phone + TAB
        + "fromEventId = " + this.fromEventId + TAB + "active = " + this.active
        + TAB + "dateInserted = "
        + DateTimeFormat.convertToString( this.dateInserted ) + TAB
        + "dateUpdated = " + DateTimeFormat.convertToString( this.dateUpdated )
        + TAB + " )";
    return retValue;
  }

}
