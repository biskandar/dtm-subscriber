package com.beepcast.subscriber;

import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberDncDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "ClientSubscriberDncDAO" );

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

  public ClientSubscriberDncDAO() {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insert( boolean debug , ClientSubscriberDncBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      return result;
    }

    // read params

    int clientId = bean.getClientId();
    String phone = bean.getPhone();
    int synchNo = bean.getSynchNo();
    String type = bean.getType();

    // clean params

    phone = ( phone == null ) ? "" : phone.trim();
    type = ( type == null ) ? "" : type.trim();

    // compose sql

    String sqlInsert = "INSERT INTO client_subscriber_dnc ( client_id , phone ";
    sqlInsert += ", encrypt_phone , synch_no , type , active , date_inserted ";
    sqlInsert += ", date_updated ) ";
    String sqlValues = "VALUES ( " + clientId + " , '' , "
        + sqlEncryptPhoneNumber( phone ) + " , " + synchNo + " , '"
        + StringEscapeUtils.escapeSql( type ) + "' , 1 , NOW() , NOW() ) ";
    String sql = sqlInsert + sqlValues;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public ClientSubscriberDncBean selectByClientIdPhoneNumber( boolean debug ,
      int clientId , String phoneNumber ) {
    ClientSubscriberDncBean bean = null;
    if ( clientId < 1 ) {
      return bean;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      return bean;
    }

    // compose sql

    String sqlSelectFrom = sqlSelectFrom();
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";
    String sql = sqlSelectFrom + sqlWhere + sqlOrder + sqlLimit;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public ClientSubscriberDncBean selectFirstActiveRecordByClientId(
      boolean debug , int clientId ) {
    ClientSubscriberDncBean bean = null;
    if ( clientId < 1 ) {
      return bean;
    }

    // compose sql

    String sqlSelectFrom = sqlSelectFrom();
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sqlOrder = "ORDER BY id ASC ";
    String sqlLimit = "LIMIT 1 ";
    String sql = sqlSelectFrom + sqlWhere + sqlOrder + sqlLimit;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public boolean deleteById( boolean debug , int id ) {
    boolean result = false;
    if ( id < 1 ) {
      return result;
    }

    // compose sql

    String sqlUpdate = "UPDATE client_subscriber_dnc ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE ( id = " + id + " ) ";
    String sql = sqlUpdate + sqlSet + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public long deleteByClientIdSynchNo( boolean debug , int clientId ,
      int synchNo ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      return totalRecords;
    }
    if ( synchNo < 0 ) {
      return totalRecords;
    }

    // compose sql

    String sqlUpdate = "UPDATE client_subscriber_dnc ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( synch_no = " + synchNo + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sql = sqlUpdate + sqlSet + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  public long deleteByClientIdPhoneNumber( boolean debug , int clientId ,
      String phoneNumber ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      return totalRecords;
    }
    if ( StringUtils.isBlank( phoneNumber ) ) {
      return totalRecords;
    }

    // compose sql

    String sqlUpdate = "UPDATE client_subscriber_dnc ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sql = sqlUpdate + sqlSet + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  public long selectTotalByClientIdActive( boolean debug , int clientId ,
      boolean active ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      return totalRecords;
    }

    // compose sql

    String sqlSelect = "SELECT COUNT( DISTINCT id ) AS total ";
    String sqlFrom = "FROM client_subscriber_dnc ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( active = " + ( active ? 1 : 0 ) + " ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalRecords;
    }
    Iterator it = qr.iterator();
    if ( !it.hasNext() ) {
      return totalRecords;
    }
    QueryItem qi = (QueryItem) it.next();
    if ( qi == null ) {
      return totalRecords;
    }
    try {
      totalRecords = Long.parseLong( qi.getFirstValue() );
    } catch ( Exception e ) {
    }

    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlSelectFrom() {
    String sqlSelect = "SELECT id , client_id ";
    sqlSelect += ", " + sqlDecryptPhoneNumber() + " ";
    sqlSelect += ", synch_no , type , active , description ";
    sqlSelect += ", date_inserted , date_updated ";
    String sqlFrom = "FROM client_subscriber_dnc ";
    String sqlSelectFrom = sqlSelect + sqlFrom;
    return sqlSelectFrom;
  }

  private ClientSubscriberDncBean populateRecord( QueryResult qr ) {
    ClientSubscriberDncBean bean = null;
    if ( qr == null ) {
      return bean;
    }
    Iterator it = qr.iterator();
    if ( !it.hasNext() ) {
      return bean;
    }
    bean = populateRecord( (QueryItem) it.next() );
    return bean;
  }

  private ClientSubscriberDncBean populateRecord( QueryItem qi ) {
    ClientSubscriberDncBean bean = null;
    if ( qi == null ) {
      return bean;
    }

    bean = new ClientSubscriberDncBean();

    String stemp;

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // client_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setClientId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // phone
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setPhone( stemp );
    }

    stemp = (String) qi.get( 3 ); // synch_no
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setSynchNo( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 4 ); // type
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setType( stemp );
    }

    stemp = (String) qi.get( 5 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setActive( stemp.equals( "1" ) );
    }

    stemp = (String) qi.get( 6 ); // description
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDescription( stemp );
    }

    stemp = (String) qi.get( 7 ); // date_inserted
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateInserted( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 8 ); // date_updated
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
