package com.beepcast.subscriber;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberUnsubsDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberUnsubsDAO" );

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

  public ClientSubscriberUnsubsDAO() {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberUnsubsBean queryClientSubscriberUnsubsBean(
      boolean debug , int clientId , String phoneNumber ) {
    ClientSubscriberUnsubsBean bean = null;

    // compose sql

    String sqlSelect = "SELECT id , client_id , " + sqlDecryptPhoneNumber()
        + " , from_event_id , active , date_inserted , date_updated ";
    String sqlFrom = "FROM client_subscriber_unsubs ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    // execute sql
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public boolean isPhoneExist( boolean debug , int clientId , String phoneNumber ) {
    return ( queryClientSubscriberUnsubsBean( debug , clientId , phoneNumber ) != null );
  }

  public boolean insertNewPhone( boolean debug , int clientId ,
      String phoneNumber , int fromEventId ) {
    boolean result = false;

    if ( clientId < 1 ) {
      return result;
    }

    if ( StringUtils.isBlank( phoneNumber ) ) {
      return result;
    }

    String sqlInsert = "INSERT INTO client_subscriber_unsubs "
        + "( client_id , phone , encrypt_phone , from_event_id , active , date_inserted , date_updated ) ";
    String sqlValues = "VALUES ( " + clientId + " , '' , "
        + sqlEncryptPhoneNumber( phoneNumber ) + " , " + fromEventId
        + " , 1 , NOW() , NOW() ) ";

    String sql = sqlInsert + sqlValues;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public boolean deletePhone( boolean debug , int clientId , String phoneNumber ) {
    boolean result = false;

    if ( clientId < 1 ) {
      return result;
    }

    if ( StringUtils.isBlank( phoneNumber ) ) {
      return result;
    }

    String sqlUpdate = "UPDATE client_subscriber_unsubs ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public long totalActiveNumbers( boolean debug , int clientId ) {
    long totalNumbers = 0;

    if ( clientId < 1 ) {
      return totalNumbers;
    }

    String sqlSelect = "SELECT COUNT( DISTINCT id ) AS total ";
    String sqlFrom = "FROM client_subscriber_unsubs ";
    String sqlWhere = "WHERE ( client_id = " + clientId
        + " ) AND ( active = 1 ) ";

    String sql = sqlSelect + sqlFrom + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    String stemp = null;
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      Iterator iter = qr.iterator();
      if ( iter.hasNext() ) {
        QueryItem qi = (QueryItem) iter.next();
        stemp = (String) qi.getFirstValue();
      }
    }
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        totalNumbers = Long.parseLong( stemp );
      } catch ( NumberFormatException e ) {
      }
    }

    return totalNumbers;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private ClientSubscriberUnsubsBean populateRecord( QueryResult qr ) {
    ClientSubscriberUnsubsBean bean = new ClientSubscriberUnsubsBean();

    String stemp;
    int itemp;

    if ( qr == null ) {
      return bean;
    }

    Iterator iter = qr.iterator();
    if ( !iter.hasNext() ) {
      return bean;
    }

    QueryItem qi = (QueryItem) iter.next();
    if ( qi == null ) {
      return bean;
    }

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setId( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // client_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setClientId( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // phone
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setPhone( stemp );
    }

    stemp = (String) qi.get( 3 ); // from_event_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setFromEventId( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 4 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setActive( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 5 ); // date_inserted
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateInserted( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 6 ); // date_updated
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateUpdated( DateTimeFormat.convertToDate( stemp ) );
    }

    return bean;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlEncryptPhoneNumber( String phoneNumber ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_ENCRYPT('" );
    sb.append( phoneNumber );
    sb.append( "','" );
    sb.append( keyPhoneNumber );
    sb.append( "')" );
    return sb.toString();
  }

  private String sqlDecryptPhoneNumber() {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') AS phone" );
    return sb.toString();
  }

}
