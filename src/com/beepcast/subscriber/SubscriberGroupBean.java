package com.beepcast.subscriber;

public class SubscriberGroupBean {

  private int id;
  private int clientId;
  private String groupName;
  private boolean active;

  public SubscriberGroupBean() {
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

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName( String groupName ) {
    this.groupName = groupName;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive( boolean active ) {
    this.active = active;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "SubscriberGroupBean ( " + "id = " + this.id + TAB
        + "clientId = " + this.clientId + TAB + "groupName = " + this.groupName
        + TAB + "active = " + this.active + TAB + " )";
    return retValue;
  }

}
