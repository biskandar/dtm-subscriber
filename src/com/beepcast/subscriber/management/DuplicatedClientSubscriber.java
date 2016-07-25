package com.beepcast.subscriber.management;

public class DuplicatedClientSubscriber {

  private int id;
  private int clientId;
  private String phoneNumber;
  private int totalDuplicated;

  public DuplicatedClientSubscriber() {

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

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber( String phoneNumber ) {
    this.phoneNumber = phoneNumber;
  }

  public int getTotalDuplicated() {
    return totalDuplicated;
  }

  public void setTotalDuplicated( int totalDuplicated ) {
    this.totalDuplicated = totalDuplicated;
  }

}
