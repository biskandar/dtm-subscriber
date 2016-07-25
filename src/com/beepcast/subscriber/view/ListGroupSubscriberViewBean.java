package com.beepcast.subscriber.view;

import java.util.List;

public class ListGroupSubscriberViewBean {

  private int clientId;
  private String inKeywordGroupNames;
  private String exKeywordGroupNames;
  private long totalRecords;
  private List beans;

  public ListGroupSubscriberViewBean() {

  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId( int clientId ) {
    this.clientId = clientId;
  }

  public String getInKeywordGroupNames() {
    return inKeywordGroupNames;
  }

  public void setInKeywordGroupNames( String inKeywordGroupNames ) {
    this.inKeywordGroupNames = inKeywordGroupNames;
  }

  public String getExKeywordGroupNames() {
    return exKeywordGroupNames;
  }

  public void setExKeywordGroupNames( String exKeywordGroupNames ) {
    this.exKeywordGroupNames = exKeywordGroupNames;
  }

  public long getTotalRecords() {
    return totalRecords;
  }

  public void setTotalRecords( long totalRecords ) {
    this.totalRecords = totalRecords;
  }

  public List getBeans() {
    return beans;
  }

  public void setBeans( List beans ) {
    this.beans = beans;
  }

}
