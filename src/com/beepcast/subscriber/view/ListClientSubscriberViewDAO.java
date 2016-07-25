package com.beepcast.subscriber.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ListClientSubscriberViewDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ListClientSubscriberViewDAO" );

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

  public ListClientSubscriberViewDAO() {

    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public int totalBeans( int clientId , int subscriberGroupId ,
      String phoneNumber ) {
    int totalBeans = 0;

    if ( clientId < 1 ) {
      return totalBeans;
    }

    if ( StringUtils.isBlank( phoneNumber ) ) {
      return totalBeans;
    }

    // compose sql
    String sqlInnerSelectFrom = ClientSubscriberViewQuery
        .sqlSelectFrom( phoneNumber );
    String sqlInnerWhere = sqlWhere( clientId , subscriberGroupId , phoneNumber );
    String sqlInner = sqlInnerSelectFrom + sqlInnerWhere;
    String sql = "SELECT COUNT(t.csid) AS total FROM ( " + sqlInner + " ) t ";

    // execute sql
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalBeans;
    }

    // query sql
    try {
      Iterator it = qr.iterator();
      QueryItem qi = (QueryItem) it.next();
      totalBeans = Integer.parseInt( qi.getFirstValue() );
    } catch ( Exception e ) {
    }

    return totalBeans;
  }

  public List listBeans( int clientId , int subscriberGroupId ,
      String phoneNumber , int top , int limit ) {
    List listBeans = new ArrayList();

    // compose sql
    String sqlSelectFrom = ClientSubscriberViewQuery
        .sqlSelectFrom( keyPhoneNumber );
    String sqlWhere = sqlWhere( clientId , subscriberGroupId , phoneNumber );
    String sqlOrder = "ORDER BY sg.id DESC ";
    sqlOrder += ", "
        + ClientSubscriberViewQuery.sqlDecryptPhoneNumber( "cs" ,
            keyPhoneNumber , null ) + " ASC ";
    String sqlLimit = "LIMIT " + top + " , " + limit + " ";
    String sql = sqlSelectFrom + sqlWhere + sqlOrder + sqlLimit;

    // execute & select query
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    listBeans.addAll( ClientSubscriberViewQuery.populateBeans( qr ) );

    return listBeans;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlWhere( int clientId , int subscriberGroupId ,
      String phoneNumber ) {
    String sqlWhere = "WHERE ( cs.active = 1 ) ";

    sqlWhere += "AND ( cs.client_id = " + clientId + " ) ";

    if ( subscriberGroupId > 0 ) {
      sqlWhere += "AND ( cs.subscriber_group_id = " + subscriberGroupId + " ) ";
    }

    if ( ( phoneNumber != null ) && ( !phoneNumber.equals( "" ) ) ) {
      sqlWhere += "AND ( "
          + ClientSubscriberViewQuery.sqlDecryptPhoneNumber( "cs" ,
              keyPhoneNumber , null ) + " LIKE '%"
          + StringEscapeUtils.escapeSql( phoneNumber ) + "%' ) ";
    }

    return sqlWhere;
  }

}
