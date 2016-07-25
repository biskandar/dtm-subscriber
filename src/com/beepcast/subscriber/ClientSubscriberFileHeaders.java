package com.beepcast.subscriber;

import java.util.ArrayList;

public class ClientSubscriberFileHeaders {

  public static ArrayList initFileHeaders() {
    ArrayList listHeaders = new ArrayList();
    listHeaders.add( ClientSubscriberFileHeader.PHONENUMBER );
    listHeaders.add( ClientSubscriberFileHeader.DATECREATED );
    listHeaders.add( ClientSubscriberFileHeader.DATEMODIFIED );
    listHeaders.add( ClientSubscriberFileHeader.SUBSCRIBEDSTATUS );
    listHeaders.add( ClientSubscriberFileHeader.UNSUBSCRIBEDDATE );
    listHeaders.add( ClientSubscriberFileHeader.GLOBALSUBSCRIBEDSTATUS );
    listHeaders.add( ClientSubscriberFileHeader.GLOBALUNSUBSCRIBEDDATE );
    listHeaders.add( ClientSubscriberFileHeader.UNSUBSCRIBEDEVENTNAME );
    listHeaders.add( ClientSubscriberFileHeader.GLOBALVALIDSTATUS );
    listHeaders.add( ClientSubscriberFileHeader.GLOBALINVALIDDATE );
    listHeaders.add( ClientSubscriberFileHeader.GLOBALINVALIDEVENTNAME );
    listHeaders.add( ClientSubscriberFileHeader.NOTDNCREGISTEREDSTATUS );
    listHeaders.add( ClientSubscriberFileHeader.DNCREGISTEREDDATE );
    listHeaders.add( ClientSubscriberFileHeader.DESCRIPTION );
    return listHeaders;
  }

}
