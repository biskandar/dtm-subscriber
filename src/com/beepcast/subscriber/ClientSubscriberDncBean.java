package com.beepcast.subscriber;

import java.util.Date;

public class ClientSubscriberDncBean {

  private int id;
  private int clientId;
  private String phone;
  private int synchNo;
  private String type;
  private boolean active;
  private String description;
  private Date dateInserted;
  private Date dateUpdated;

  public ClientSubscriberDncBean() {
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

  public int getSynchNo() {
    return synchNo;
  }

  public void setSynchNo( int synchNo ) {
    this.synchNo = synchNo;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive( boolean active ) {
    this.active = active;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
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
    retValue = "ClientSubscriberDncBean ( " + "id = " + this.id + TAB
        + "clientId = " + this.clientId + TAB + "phone = " + this.phone + TAB
        + "synchNo = " + this.synchNo + TAB + "type = " + this.type + TAB
        + "active = " + this.active + TAB + " )";
    return retValue;
  }

}
