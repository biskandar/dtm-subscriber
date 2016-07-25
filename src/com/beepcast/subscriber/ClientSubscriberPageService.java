package com.beepcast.subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.beepcast.database.ConnectionWrapper;
import com.beepcast.database.DatabaseLibrary;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.mysql.jdbc.CommunicationsException;

public class ClientSubscriberPageService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberPageService" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private DatabaseLibrary dbLib;

  private final String keyPhoneNumber;
  private final String keyCustom;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberPageService() {

    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
    keyCustom = encryptApp.getKeyValue( EncryptApp.KEYNAME_CUSTOM );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List select( int groupSubscriberId , Boolean subscribedStatus ,
      int top , int limit ) {
    List list = new ArrayList();

    // validate group subscriber id
    if ( groupSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to select , found zero groupSubscriberId" );
      return list;
    }

    // header log
    String headerLog = SubscriberGroupCommon.headerLog( groupSubscriberId );

    // compose sql
    String sql = sqlSelectFromWhere( groupSubscriberId , subscribedStatus ,
        top , limit );

    // execute sql
    String databaseName = "profiledb";
    ConnectionWrapper conn = dbLib.getReaderConnection( databaseName );
    if ( ( conn != null ) && ( conn.isConnected() ) ) {
      Statement statement = null;
      ResultSet resultSet = null;
      try {
        statement = conn.createStatement();
        if ( statement != null ) {
          DLog.debug( lctx , headerLog + "Perform " + sql );
          resultSet = statement.executeQuery( sql );
        }
        if ( resultSet != null ) {
          while ( resultSet.next() ) {

            ClientSubscriberBean csBean = ClientSubscriberFactory
                .createClientSubscriberBean( 0 , groupSubscriberId ,
                    resultSet.getString( "phone" ) ,
                    resultSet.getString( "cust_ref_id" ) ,
                    resultSet.getString( "cust_ref_code" ) );

            csBean.setId( resultSet.getInt( "id" ) );
            csBean.setDateInserted( DateTimeFormat.convertToDate( resultSet
                .getString( "date_inserted" ) ) );

            ClientSubscriberCustomBean csCustomBean = ClientSubscriberFactory
                .createClientSubscriberCustomBean( csBean.getId() ,
                    resultSet.getString( "custom_0" ) ,
                    resultSet.getString( "custom_1" ) ,
                    resultSet.getString( "custom_2" ) ,
                    resultSet.getString( "custom_3" ) ,
                    resultSet.getString( "custom_4" ) ,
                    resultSet.getString( "custom_5" ) ,
                    resultSet.getString( "custom_6" ) ,
                    resultSet.getString( "custom_7" ) ,
                    resultSet.getString( "custom_8" ) ,
                    resultSet.getString( "custom_9" ) ,
                    resultSet.getString( "custom_10" ) ,
                    resultSet.getString( "custom_11" ) ,
                    resultSet.getString( "custom_12" ) ,
                    resultSet.getString( "custom_13" ) ,
                    resultSet.getString( "custom_14" ) ,
                    resultSet.getString( "custom_15" ) ,
                    resultSet.getString( "custom_16" ) ,
                    resultSet.getString( "custom_17" ) ,
                    resultSet.getString( "custom_18" ) ,
                    resultSet.getString( "custom_19" ) ,
                    resultSet.getString( "custom_20" ) ,
                    resultSet.getString( "custom_21" ) ,
                    resultSet.getString( "custom_22" ) ,
                    resultSet.getString( "custom_23" ) ,
                    resultSet.getString( "custom_24" ) ,
                    resultSet.getString( "custom_25" ) ,
                    resultSet.getString( "custom_26" ) ,
                    resultSet.getString( "custom_27" ) ,
                    resultSet.getString( "custom_28" ) ,
                    resultSet.getString( "custom_29" ) ,
                    resultSet.getString( "custom_30" ) );

            csCustomBean.setId( resultSet.getInt( "custom_id" ) );

            csBean.setCsCustomBean( csCustomBean );

            list.add( csBean );

          }
        }
      } catch ( CommunicationsException communicationsException ) {
        DLog.warning( lctx , headerLog + "[" + databaseName
            + "] Database query failed , " + communicationsException );
      } catch ( SQLException sqlException ) {
        DLog.warning( lctx , headerLog + "[" + databaseName
            + "] Database query failed , " + sqlException );
      }
      try {
        if ( resultSet != null ) {
          resultSet.close();
          resultSet = null;
        }
      } catch ( SQLException e ) {
        DLog.warning( lctx , headerLog + "[" + databaseName
            + "] Failed to close the ResultSet object , " + e );
      }
      try {
        if ( statement != null ) {
          statement.close();
          statement = null;
        }
      } catch ( SQLException e ) {
        DLog.warning( lctx , headerLog + "[" + databaseName
            + "] Failed to close the Statement object , " + e );
      }
      conn.disconnect( true );
    }

    return list;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlSelectFromWhere( int groupSubscriberId ,
      Boolean subscribedStatus , int top , int limit ) {

    String sqlSelect = "SELECT cs.id , " + sqlDecryptPhoneNumber( "cs" ) + " ";

    sqlSelect += ", cs.date_inserted ";
    sqlSelect += ", cs.subscribed , cs.date_subscribed ";
    sqlSelect += ", cs.global_subscribed , cs.date_global_subscribed ";
    sqlSelect += ", cs.cust_ref_id , cs.cust_ref_code , cs.date_send ";
    sqlSelect += ", cs.description , cs.global_invalid , cs.date_global_invalid ";
    sqlSelect += ", cs.global_dnc , cs.date_global_dnc ";

    sqlSelect += ", cs.subscribed_app_id ";

    sqlSelect += ", csc.id AS custom_id ";

    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_0" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_1" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_2" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_3" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_4" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_5" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_6" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_7" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_8" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_9" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_10" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_11" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_12" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_13" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_14" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_15" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_16" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_17" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_18" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_19" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_20" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_21" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_22" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_23" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_24" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_25" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_26" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_27" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_28" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_29" );
    sqlSelect += ", " + sqlDecryptCustom( "csc" , "custom_30" );

    sqlSelect += ", csc.date_updated ";

    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "LEFT OUTER JOIN client_subscriber_custom csc ON "
        + "( cs.id = csc.client_subscriber_id ) ";

    String sqlWhere = "WHERE ( cs.subscriber_group_id = " + groupSubscriberId
        + " ) ";
    if ( subscribedStatus != null ) {
      sqlWhere += "AND ( ";
      if ( subscribedStatus.booleanValue() ) {
        // only for subscribed numbers
        sqlWhere += "( cs.active = 1 ) AND ( cs.global_invalid = 0 ) ";
        sqlWhere += "AND ( cs.global_dnc = 0 ) AND ( cs.subscribed = 1 ) ";
        sqlWhere += "AND ( cs.global_subscribed = 1 ) ";
      } else {
        // only for un-subscribed numbers
        sqlWhere += "( cs.active = 0 ) OR ( cs.global_invalid = 1 ) ";
        sqlWhere += "OR ( cs.global_dnc = 1 ) OR ( cs.subscribed = 0 ) ";
        sqlWhere += "OR ( cs.global_subscribed = 0 ) ";
      }
      sqlWhere += ") ";
    }

    String sqlOrder = "ORDER BY cs.id ASC ";

    String sqlLimit = "LIMIT " + top + " , " + limit;

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    return sql;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlDecryptPhoneNumber( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') AS phone " );
    return sb.toString();
  }

  private String sqlDecryptCustom( String tableAlias , String customField ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_" + customField + ",'" );
    sb.append( keyCustom );
    sb.append( "') AS " );
    sb.append( customField );
    return sb.toString();
  }

}
