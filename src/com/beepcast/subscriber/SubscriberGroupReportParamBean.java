package com.beepcast.subscriber;

public class SubscriberGroupReportParamBean {

  private int id;
  private int subscriberGroupId;
  private String field;
  private String value;
  private String description;
  private boolean active;

  public SubscriberGroupReportParamBean() {
  }

  public int getId() {
    return id;
  }

  public void setId( int id ) {
    this.id = id;
  }

  public int getSubscriberGroupId() {
    return subscriberGroupId;
  }

  public void setSubscriberGroupId( int subscriberGroupId ) {
    this.subscriberGroupId = subscriberGroupId;
  }

  public String getField() {
    return field;
  }

  public void setField( String field ) {
    this.field = field;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
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
    retValue = "SubscriberGroupReportParamBean ( " + "id = " + this.id + TAB
        + "subscriberGroupId = " + this.subscriberGroupId + TAB + "field = "
        + this.field + TAB + "value = " + this.value + TAB + "description = "
        + this.description + TAB + "active = " + this.active + TAB + " )";
    return retValue;
  }

}
