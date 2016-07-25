package com.beepcast.subscriber.view;

import java.util.Date;

public class GroupSubscriberViewBean {

  private int id;
  private String groupName;
  private int totalSubcriber;
  private int totalSubscribed;
  private Date dateInserted;
  private Date dateUpdated;

  public GroupSubscriberViewBean() {
  }

  public int getId() {
    return id;
  }

  public void setId( int id ) {
    this.id = id;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName( String groupName ) {
    this.groupName = groupName;
  }

  public int getTotalSubcriber() {
    return totalSubcriber;
  }

  public void setTotalSubcriber( int totalSubcriber ) {
    this.totalSubcriber = totalSubcriber;
  }

  public int getTotalSubscribed() {
    return totalSubscribed;
  }

  public void setTotalSubscribed( int totalSubscribed ) {
    this.totalSubscribed = totalSubscribed;
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
    retValue = "GroupSubscriberViewBean ( " + "id = " + this.id + TAB
        + "groupName = " + this.groupName + TAB + "totalSubcriber = "
        + this.totalSubcriber + TAB + "totalSubscribed = "
        + this.totalSubscribed + TAB + "dateInserted = " + this.dateInserted
        + TAB + "dateUpdated = " + this.dateUpdated + TAB + " )";
    return retValue;
  }

}
