package com.beepcast.subscriber.view;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberViewDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "ClientSubscriberViewDAO" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private DatabaseLibrary dbLib;
  private final String keyPhoneNumber;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberViewDAO() {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List searchByPhoneNumber( boolean debug , int clientId ,
      String phoneNumber , int limit ) {
    List list = null;

    String sqlSelectFrom = ClientSubscriberViewQuery
        .sqlSelectFrom( keyPhoneNumber );
    String sqlWhere = "WHERE ( cs.active = 1 ) ";
    sqlWhere += "AND ( cs.client_id = " + clientId + " ) ";
    sqlWhere += "AND ( "
        + ClientSubscriberViewQuery.sqlDecryptPhoneNumber( "cs" ,
            keyPhoneNumber , null ) + " LIKE '%"
        + StringEscapeUtils.escapeSql( phoneNumber ) + "%' ) ";

    String sqlOrder = "ORDER BY sg.id DESC ";

    String sqlLimit = "LIMIT " + limit + " ";

    String sql = sqlSelectFrom + sqlWhere + sqlOrder + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );

    list = ClientSubscriberViewQuery.populateBeans( qr );

    return list;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  // ...

}
