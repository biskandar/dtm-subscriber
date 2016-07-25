package com.beepcast.subscriber;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.beepcast.database.ConnectionWrapper;
import com.beepcast.database.DatabaseLibrary;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.mysql.jdbc.CommunicationsException;

public class ClientSubscriberNumberService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberNumberService" );

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

  public ClientSubscriberNumberService() {

    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public Map generateMapNumbers( int groupSubscriberId ,
      Boolean subscribedStatus ) {
    Map map = new HashMap();

    // validate group subscriber id
    if ( groupSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to generate map numbers "
          + ", found zero groupSubscriberId" );
      return map;
    }

    // header log
    String headerLog = SubscriberGroupCommon.headerLog( groupSubscriberId );

    // compose sql
    String sql = sqlSelectFromWhere( groupSubscriberId , subscribedStatus );

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
            map.put( resultSet.getString( "phone" ) , resultSet.getInt( "id" ) );
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

    return map;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlSelectFromWhere( int groupSubscriberId ,
      Boolean subscribedStatus ) {

    String sqlSelect = "SELECT cs.id , " + sqlDecryptPhoneNumber( "cs" ) + " ";
    String sqlFrom = "FROM client_subscriber cs ";
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

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

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

}
