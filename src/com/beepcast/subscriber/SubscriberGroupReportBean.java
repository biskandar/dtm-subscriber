package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SubscriberGroupReportBean {

  private int id;
  private int subscriberGroupId;
  private long totalUploaded;
  private long totalSubscribed;
  private long totalDuplicatedNumber;
  private long totalInvalidNumber;
  private boolean active;
  private Map params;

  public SubscriberGroupReportBean() {
    params = new HashMap();
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

  public long getTotalUploaded() {
    return totalUploaded;
  }

  public void setTotalUploaded( long totalUploaded ) {
    this.totalUploaded = totalUploaded;
  }

  public long getTotalSubscribed() {
    return totalSubscribed;
  }

  public void setTotalSubscribed( long totalSubscribed ) {
    this.totalSubscribed = totalSubscribed;
  }

  public long getTotalDuplicatedNumber() {
    return totalDuplicatedNumber;
  }

  public void setTotalDuplicatedNumber( long totalDuplicatedNumber ) {
    this.totalDuplicatedNumber = totalDuplicatedNumber;
  }

  public long getTotalInvalidNumber() {
    return totalInvalidNumber;
  }

  public void setTotalInvalidNumber( long totalInvalidNumber ) {
    this.totalInvalidNumber = totalInvalidNumber;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive( boolean active ) {
    this.active = active;
  }

  public void addParam( String field , String value ) {
    if ( StringUtils.isBlank( field ) ) {
      return;
    }
    synchronized ( params ) {
      params.put( field , value );
    }
  }

  public String getParamValue( String field ) {
    String value = null;
    if ( StringUtils.isBlank( field ) ) {
      return value;
    }
    synchronized ( params ) {
      value = (String) params.get( field );
    }
    return value;
  }

  public List getParamFields() {
    List list = new ArrayList( params.keySet() );
    return list;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "SubscriberGroupReportBean ( " + "id = " + this.id + TAB
        + "subscriberGroupId = " + this.subscriberGroupId + TAB
        + "totalUploaded = " + this.totalUploaded + TAB + "totalSubscribed = "
        + this.totalSubscribed + TAB + "totalDuplicatedNumber = "
        + this.totalDuplicatedNumber + TAB + "totalInvalidNumber = "
        + this.totalInvalidNumber + TAB + "active = " + this.active + TAB
        + "params = " + this.params + TAB + " )";
    return retValue;
  }

}
