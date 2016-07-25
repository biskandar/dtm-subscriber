package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberDncCleaner extends Thread {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberDncCleaner" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private boolean debug;

  private DatabaseLibrary dbLib;
  private final String keyPhoneNumber;

  private int clientId;
  private int synchNoPrev;
  private int synchNoNext;
  private long sleep;

  private String headerLog;

  private List listGroupIds;
  private List listDncUnregisteredNumbers;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberDncCleaner( int clientId , int synchNoPrev ,
      int synchNoNext , long sleep ) {

    long deltaTime = System.currentTimeMillis();

    subscriberApp = SubscriberApp.getInstance();
    debug = subscriberApp.isDebug();

    dbLib = DatabaseLibrary.getInstance();
    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );

    this.clientId = clientId;
    this.synchNoPrev = synchNoPrev;
    this.synchNoNext = synchNoNext;
    this.sleep = sleep;

    if ( this.sleep < 50 ) {
      this.sleep = 50;
    }

    headerLog = "[DncCleaner-" + clientId + "] [SynchNo-" + synchNoNext + "] ";
    DLog.debug( lctx , headerLog + "Executing dnc cleaner with "
        + ": clientId = " + clientId + " , synchNoPrev = " + synchNoPrev
        + " , synchNoNext = " + synchNoNext + " , sleep = " + sleep + " ms" );

    if ( clientId < 1 ) {
      DLog.warning( lctx , headerLog + "Failed to initialized "
          + ", found zero client id" );
      return;
    }

    listGroupIds = listGroupIds();
    if ( listGroupIds != null ) {
      DLog.debug( lctx ,
          headerLog + "Read total groupIds = " + listGroupIds.size() );
    }

    listDncUnregisteredNumbers = listDncUnregisteredNumbers();
    if ( listDncUnregisteredNumbers != null ) {
      DLog.debug( lctx , headerLog + "Read total dncUnregisteredNumbers = "
          + listDncUnregisteredNumbers.size() );
    }

    deltaTime = System.currentTimeMillis() - deltaTime;
    DLog.debug( lctx , "Initialized dnc unregistered number(s) , takes "
        + deltaTime + " ms" );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void run() {

    // must be params

    if ( ( listGroupIds == null ) || ( listGroupIds.size() < 1 ) ) {
      DLog.debug( lctx , headerLog + "Do not start the cleaner worker "
          + ", found empty listGroupIds" );
      return;
    }
    if ( ( listDncUnregisteredNumbers == null )
        || ( listDncUnregisteredNumbers.size() < 1 ) ) {
      DLog.debug( lctx , headerLog + "Do not start the cleaner worker "
          + ", found empty listDncUnregisteredNumbers" );
      return;
    }

    // log started and trap delta time
    long deltaTime = System.currentTimeMillis();
    DLog.debug( lctx , headerLog + "Thread started" );

    // make it into the string of numbers
    String strDncUnregisteredNumbers = "\""
        + StringUtils.join( listDncUnregisteredNumbers , "\",\"" ) + "\"";

    // update dnc unregistered numbers across all the group id
    long totalRecords = 0;
    Iterator iterGroupIds = listGroupIds.iterator();
    while ( iterGroupIds.hasNext() ) {
      String groupId = (String) iterGroupIds.next();
      if ( groupId == null ) {
        continue;
      }
      try {
        Thread.sleep( sleep );
        totalRecords = updateDncUnregistered( strDncUnregisteredNumbers ,
            groupId );
        DLog.debug( lctx , headerLog + "Updated to clean dnc numbers "
            + "from the list group id = " + groupId + " , total effected = "
            + totalRecords + " record(s)" );
      } catch ( Exception e ) {
        DLog.warning( lctx , headerLog
            + "Failed to update clean dnc numbers , " + e );
      }
    }

    // log stopped and calculate delta time
    deltaTime = System.currentTimeMillis() - deltaTime;
    DLog.debug( lctx , headerLog + "Thread stopped ( takes " + deltaTime
        + " ms )" );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private List listGroupIds() {
    List listGroupIds = new ArrayList();

    // compose sql

    String sqlSelect = "SELECT id ";
    String sqlFrom = "FROM subscriber_group ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( active = 1 ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      Iterator it = qr.iterator();
      while ( it.hasNext() ) {
        QueryItem qi = (QueryItem) it.next();
        if ( qi == null ) {
          continue;
        }
        String groupId = qi.getFirstValue();
        if ( StringUtils.isBlank( groupId ) ) {
          continue;
        }
        listGroupIds.add( groupId );
      }
    }

    return listGroupIds;
  }

  private List listDncUnregisteredNumbers() {
    List listNumbers = null;

    // compose sql

    String sql = sqlDncUnregisteredNumbers2();

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return listNumbers;
    }

    // process records

    listNumbers = new ArrayList();
    Iterator it = qr.iterator();
    while ( it.hasNext() ) {
      QueryItem qi = (QueryItem) it.next();
      if ( qi == null ) {
        continue;
      }
      String phoneNumber = qi.getFirstValue();
      if ( StringUtils.isBlank( phoneNumber ) ) {
        continue;
      }
      listNumbers.add( phoneNumber );
    }

    return listNumbers;
  }

  public long updateDncUnregistered( String strDncUnregisteredNumbers ,
      String groupId ) {
    long totalRecords = 0;

    // prepare the sql

    String sqlUpdate = "UPDATE client_subscriber cs ";
    String sqlSet = "SET cs.global_dnc = 0 ";
    sqlSet += ", cs.date_global_dnc = NOW() ";
    String sqlWhere = "WHERE ( cs.subscriber_group_id = " + groupId + " ) ";
    sqlWhere += "AND ( " + sqlDecryptPhoneNumber( "cs" , false ) + " IN ( "
        + strDncUnregisteredNumbers + " ) ) ";
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

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlDncUnregisteredNumbers1() {

    String sqlInnerPrevSelect = "SELECT csd.encrypt_phone ";
    String sqlInnerPrevFrom = "FROM client_subscriber_dnc csd ";
    String sqlInnerPrevWhere = "WHERE ( csd.client_id = " + clientId + " ) ";
    sqlInnerPrevWhere += "AND ( csd.synch_no = " + synchNoPrev + " ) ";
    String sqlInnerPrev = sqlInnerPrevSelect + sqlInnerPrevFrom
        + sqlInnerPrevWhere;

    String sqlInnerNextSelect = "SELECT csd.encrypt_phone ";
    String sqlInnerNextFrom = "FROM client_subscriber_dnc csd ";
    String sqlInnerNextWhere = "WHERE ( csd.client_id = " + clientId + " ) ";
    sqlInnerNextWhere += "AND ( csd.synch_no = " + synchNoNext + " ) ";
    sqlInnerNextWhere += "AND ( csd.active = 1 ) ";
    String sqlInnerNext = sqlInnerNextSelect + sqlInnerNextFrom
        + sqlInnerNextWhere;

    String sqlSelect = "SELECT " + sqlDecryptPhoneNumber( "csd_a" , true )
        + " ";
    String sqlFrom = "FROM ( " + sqlInnerPrev + " ) csd_a ";
    sqlFrom += "LEFT OUTER JOIN ( " + sqlInnerNext + " ) csd_b ";
    sqlFrom += "ON ( csd_b.encrypt_phone = csd_a.encrypt_phone ) ";
    String sqlWhere = "WHERE ( csd_b.encrypt_phone IS NULL ) ";
    String sqlOrder = "ORDER BY " + sqlDecryptPhoneNumber( "csd_a" , false )
        + " ASC";
    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

    return sql;
  }

  private String sqlDncUnregisteredNumbers2() {
    String sql = "";

    sql += "SELECT " + sqlDecryptPhoneNumber( "csd2" , true ) + " ";
    sql += "FROM ( ";
    sql += "  SELECT csd1.encrypt_phone , SUM( csd1.synch_no ) AS total ";
    sql += "  FROM client_subscriber_dnc csd1 ";
    sql += "  WHERE ( csd1.client_id = " + clientId + " ) ";
    sql += "    AND ( ";
    sql += "      ( csd1.synch_no = " + synchNoPrev + " ) ";
    sql += "      OR ( ";
    sql += "        ( csd1.synch_no = " + synchNoNext + " ) ";
    sql += "        AND ( csd1.active = 1 ) ";
    sql += "      ) ";
    sql += "    ) ";
    sql += "  GROUP BY csd1.encrypt_phone ";
    sql += ") csd2 ";
    sql += "WHERE ( csd2.total = " + synchNoPrev + " ) ";

    return sql;
  }

  private String sqlEncryptPhoneNumber( String phoneNumber ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_ENCRYPT('" );
    sb.append( phoneNumber );
    sb.append( "','" );
    sb.append( keyPhoneNumber );
    sb.append( "')" );
    return sb.toString();
  }

  private String sqlDecryptPhoneNumber( String tblAlias , boolean addAsPhone ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" );
    if ( ( tblAlias != null ) && ( !tblAlias.equals( "" ) ) ) {
      sb.append( tblAlias );
      sb.append( "." );
    }
    sb.append( "encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "')" );
    if ( addAsPhone ) {
      sb.append( " AS phone" );
    }
    return sb.toString();
  }

}
