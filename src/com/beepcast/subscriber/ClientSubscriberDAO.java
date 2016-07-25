package com.beepcast.subscriber;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.beepcast.subscriber.common.DateTimeUtils;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "ClientSubscriberDAO" );

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

  public ClientSubscriberDAO() {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insertClientSubscriber( boolean debug ,
      ClientSubscriberBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      return result;
    }

    // get property
    int clientId = bean.getClientId();
    int subscriberGroupId = bean.getSubscriberGroupId();
    String phone = bean.getPhone();

    int subscribed = bean.getSubscribed() > 0 ? 1 : 0;
    int subscribedEventId = bean.getSubscribedEventId();
    Date dateSubscribed = bean.getDateSubscribed();

    int globalSubscribed = bean.getGlobalSubscribed() > 0 ? 1 : 0;
    Date dateGlobalSubscribed = bean.getDateGlobalSubscribed();

    String customerReferenceId = bean.getCustomerReferenceId();
    String customerReferenceCode = bean.getCustomerReferenceCode();
    Date dateSend = bean.getDateSend();
    String description = bean.getDescription();

    int globalInvalid = bean.getGlobalInvalid();
    Date dateGlobalInvalid = bean.getDateGlobalInvalid();
    int globalDnc = bean.getGlobalDnc();
    Date dateGlobalDnc = bean.getDateGlobalDnc();

    int active = bean.getActive() > 0 ? 1 : 0;

    // validate property
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to insert clientSubscriber "
          + ", found zero clientId" );
      return result;
    }
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to insert clientSubscriber "
          + ", found zero subscriberGroupId" );
      return result;
    }
    if ( StringUtils.isBlank( phone ) ) {
      DLog.warning( lctx , "Failed to insert clientSubscriber "
          + ", found empty encrypt phone" );
      return result;
    }

    // clean property
    dateSubscribed = ( dateSubscribed == null ) ? new Date() : dateSubscribed;
    dateGlobalSubscribed = ( dateGlobalSubscribed == null ) ? new Date()
        : dateGlobalSubscribed;
    customerReferenceId = ( customerReferenceId == null ) ? ""
        : customerReferenceId;
    customerReferenceCode = ( customerReferenceCode == null ) ? ""
        : customerReferenceCode;
    description = ( description == null ) ? "" : description;

    // null-able date time format
    String strDateSend = ( dateSend == null ) ? "NULL" : "'"
        + DateTimeFormat.convertToString( dateSend ) + "'";
    String strDateGlobalInvalid = ( dateGlobalInvalid == null ) ? "NULL" : "'"
        + DateTimeFormat.convertToString( dateGlobalInvalid ) + "'";
    String strDateGlobalDnc = ( dateGlobalDnc == null ) ? "NULL" : "'"
        + DateTimeFormat.convertToString( dateGlobalDnc ) + "'";

    // execute sql
    String sqlInsert = "INSERT INTO client_subscriber ";
    sqlInsert += "( client_id , subscriber_group_id , phone , encrypt_phone ";
    sqlInsert += ", subscribed , subscribed_event_id , date_subscribed ";
    sqlInsert += ", global_subscribed , date_global_subscribed , cust_ref_id ";
    sqlInsert += ", cust_ref_code , date_send , description , global_invalid ";
    sqlInsert += ", date_global_invalid , global_dnc , date_global_dnc ";
    sqlInsert += ", active , date_inserted ) ";
    String sqlValues = "VALUES ";
    sqlValues += "( " + clientId + " , " + subscriberGroupId + " , '' , "
        + sqlEncryptPhoneNumber( phone ) + " , " + subscribed + " , "
        + subscribedEventId + " , '"
        + DateTimeFormat.convertToString( dateSubscribed ) + "' , "
        + globalSubscribed + " , '"
        + DateTimeFormat.convertToString( dateGlobalSubscribed ) + "' , '"
        + StringEscapeUtils.escapeSql( customerReferenceId ) + "' , '"
        + StringEscapeUtils.escapeSql( customerReferenceCode ) + "' , "
        + strDateSend + " , '" + StringEscapeUtils.escapeSql( description )
        + "' , " + globalInvalid + " , " + strDateGlobalInvalid + " , "
        + globalDnc + " , " + strDateGlobalDnc + " , " + active + " , NOW() ) ";
    String sql = sqlInsert + sqlValues;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public ClientSubscriberBean getClientSubscriberBean( boolean debug ,
      int clientSubscriberId ) {
    ClientSubscriberBean bean = null;

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM client_subscriber ";
    String sqlWhere = "WHERE ( id = " + clientSubscriberId + " ) ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public ClientSubscriberBean getClientSubscriberBean( boolean debug ,
      int clientId , int subscriberGroupId , String phoneNumber ) {
    ClientSubscriberBean bean = null;

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM client_subscriber ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public boolean deleteClientSubscriberBean( boolean debug ,
      int clientSubscriberId ) {
    boolean result = false;
    if ( clientSubscriberId < 1 ) {
      return result;
    }

    // compose sql
    String sqlDelete = "DELETE FROM client_subscriber ";
    String sqlWhere = "WHERE ( id = " + clientSubscriberId + " ) ";
    String sql = sqlDelete + sqlWhere;

    // log
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    // execute sql
    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public boolean doSubscribed( boolean debug , int clientId ,
      String phoneNumber , int subscriberGroupId , int subscribedEventId ,
      String customerReferenceId , String customerReferenceCode ) {
    boolean result = false;

    String sqlUpdate = "UPDATE client_subscriber ";

    String sqlSet = "SET subscribed = 1 , subscribed_event_id = "
        + subscribedEventId + " , date_subscribed = NOW() ";
    sqlSet += ", global_subscribed = 1 , date_global_subscribed = NOW() ";
    if ( customerReferenceId != null ) {
      sqlSet += ", cust_ref_id = '"
          + StringEscapeUtils.escapeSql( customerReferenceId ) + "' ";
    }
    if ( customerReferenceCode != null ) {
      sqlSet += ", cust_ref_code = '"
          + StringEscapeUtils.escapeSql( customerReferenceCode ) + "' ";
    }

    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public boolean doUnsubscribed( boolean debug , int clientId ,
      String phoneNumber , int subscriberGroupId , int subscribedEventId ) {
    boolean result = false;

    String sqlUpdate = "UPDATE client_subscriber ";
    String sqlSet = "SET subscribed = 0 ";
    sqlSet += ", subscribed_event_id = " + subscribedEventId + " ";
    sqlSet += ", date_subscribed = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public int doGlobalUnsubscribed( boolean debug , int clientId ,
      String phoneNumber , int subscriberGroupId ) {
    int totalRecords = 0;

    String sqlUpdate = "UPDATE client_subscriber ";
    String sqlSet = "SET global_subscribed = 0 ";
    sqlSet += ", date_global_subscribed = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";
    if ( subscriberGroupId > 0 ) {
      sqlWhere += "AND ( subscriber_group_id <> " + subscriberGroupId + " ) ";
    }

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  public int doGlobalInvalid( boolean debug , int clientId ,
      String phoneNumber , boolean globalInvalid ) {
    int totalRecords = 0;

    String sqlUpdate = "UPDATE client_subscriber ";
    String sqlSet = "SET global_invalid = " + ( globalInvalid ? 1 : 0 ) + " ";
    sqlSet += ", date_global_invalid = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  public int doGlobalDnc( boolean debug , int clientId , String phoneNumber ,
      boolean globalDnc ) {
    int totalRecords = 0;

    String sqlUpdate = "UPDATE client_subscriber ";
    String sqlSet = "SET global_dnc = " + ( globalDnc ? 1 : 0 ) + " ";
    sqlSet += ", date_global_dnc = NOW() ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phoneNumber )
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  public boolean isExistClientSubscriber( ClientSubscriberBean bean ) {
    boolean result = false;

    if ( bean == null ) {
      return result;
    }

    // get property
    int clientId = bean.getClientId();
    int subscriberGroupId = bean.getSubscriberGroupId();
    String phone = bean.getPhone();

    // validate property
    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to validate isExist clientSubscriber "
          + ", found null clientId" );
      return result;
    }
    if ( subscriberGroupId < 1 ) {
      DLog.warning( lctx , "Failed to validate isExist clientSubscriber "
          + ", found null subscriberGroupId" );
      return result;
    }
    if ( ( phone == null ) || ( phone.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to validate isExist clientSubscriber "
          + ", found null phone" );
      return result;
    }

    String sqlSelect = "SELECT id ";
    String sqlFrom = "FROM client_subscriber ";
    String sqlWhere = "WHERE ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( encrypt_phone = " + sqlEncryptPhoneNumber( phone )
        + " ) ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlLimit;

    // DLog.debug( lctx , "Perform " + sql );

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public boolean updateClientSubscriberProfile( boolean debug ,
      ClientSubscriberBean bean , boolean override ) {
    boolean result = false;
    if ( bean == null ) {
      return result;
    }

    // read params
    String custRefId = bean.getCustomerReferenceId();
    String custRefCode = bean.getCustomerReferenceCode();
    Date dateSend = bean.getDateSend();
    String description = bean.getDescription();

    // compose sql
    String sqlUpdate = "UPDATE client_subscriber ";

    StringBuffer sqlSet = null;
    if ( override ) {
      // clean params
      custRefId = ( custRefId == null ) ? "" : custRefId.trim();
      custRefCode = ( custRefCode == null ) ? "" : custRefCode.trim();
      String strDateSend = ( dateSend == null ) ? "NULL" : "'"
          + DateTimeFormat.convertToString( dateSend ) + "'";
      description = ( description == null ) ? "" : description.trim();
      // compose sql set
      sqlSet = new StringBuffer();
      sqlSet.append( "SET cust_ref_id = '"
          + StringEscapeUtils.escapeSql( custRefId ) + "' " );
      sqlSet.append( ", cust_ref_code = '"
          + StringEscapeUtils.escapeSql( custRefCode ) + "' " );
      sqlSet.append( ", date_send = " + strDateSend + " " );
      sqlSet.append( ", description = '"
          + StringEscapeUtils.escapeSql( description ) + "' " );
    } else {
      if ( custRefId != null ) {
        if ( sqlSet == null ) {
          sqlSet = new StringBuffer( "SET " );
        } else {
          sqlSet.append( ", " );
        }
        sqlSet.append( "cust_ref_id = '" );
        sqlSet.append( StringEscapeUtils.escapeSql( custRefId.trim() ) );
        sqlSet.append( "' " );
      }
      if ( custRefCode != null ) {
        if ( sqlSet == null ) {
          sqlSet = new StringBuffer( "SET " );
        } else {
          sqlSet.append( ", " );
        }
        sqlSet.append( "cust_ref_code = '" );
        sqlSet.append( StringEscapeUtils.escapeSql( custRefCode.trim() ) );
        sqlSet.append( "' " );
      }
      if ( dateSend != null ) {
        if ( sqlSet == null ) {
          sqlSet = new StringBuffer( "SET " );
        } else {
          sqlSet.append( ", " );
        }
        sqlSet.append( "date_send = '" );
        sqlSet.append( DateTimeFormat.convertToString( dateSend ) );
        sqlSet.append( "' " );
      }
      if ( description != null ) {
        if ( sqlSet == null ) {
          sqlSet = new StringBuffer( "SET " );
        } else {
          sqlSet.append( ", " );
        }
        sqlSet.append( "description = '" );
        sqlSet.append( StringEscapeUtils.escapeSql( description.trim() ) );
        sqlSet.append( "' " );
      }
    }
    if ( sqlSet == null ) {
      return result;
    }

    String sqlWhere = "WHERE ( id = " + bean.getId() + " ) ";
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

  public boolean updateRsvpProfile( boolean debug , int clientId ,
      int subscriberGroupId , String phoneNumber , String rsvpStatus ) {
    boolean result = false;

    String sqlUpdate = "UPDATE client_subscriber ";
    String sqlSet = "SET rsvp = '" + StringEscapeUtils.escapeSql( rsvpStatus )
        + "' , date_rsvp = NOW() ";
    String sqlWhere = "WHERE ( active = 1 ) ";
    sqlWhere += "AND ( client_id = " + clientId + " ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
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

  public long getTotalSubscriber( int sgId , boolean allowDuplicated ,
      boolean useDateSend ) {
    long total = 0;

    String sql = null;

    String dateSendMin = DateTimeUtils.sqlDtmDaysBefore( 0 );

    if ( allowDuplicated ) {

      String sqlSelect = "SELECT COUNT(cs.id) as 'total' ";
      String sqlFrom = "FROM client_subscriber cs ";
      String sqlWhere = "WHERE ( cs.active = 1 ) ";
      sqlWhere += "AND ( cs.global_invalid = 0 ) ";
      sqlWhere += "AND ( cs.global_dnc = 0 ) ";
      sqlWhere += "AND ( cs.subscribed = 1 ) ";
      sqlWhere += "AND ( cs.global_subscribed = 1 ) ";
      if ( useDateSend ) {
        sqlWhere += "AND ( cs.date_send IS NOT NULL ) ";
        sqlWhere += "AND ( cs.date_send > '" + dateSendMin + "' ) ";
      }
      sqlWhere += "AND ( cs.subscriber_group_id = " + sgId + " ) ";
      sql = sqlSelect + sqlFrom + sqlWhere;

    } else {

      String sqlInnerSelect = "SELECT MAX(cs1.id) AS id ";
      String sqlInnerFrom = "FROM client_subscriber cs1 ";
      String sqlInnerWhere = "WHERE ( cs1.active = 1 ) ";
      sqlInnerWhere += "AND ( cs1.global_invalid = 0 ) ";
      sqlInnerWhere += "AND ( cs1.global_dnc = 0 ) ";
      sqlInnerWhere += "AND ( cs1.subscribed = 1 ) ";
      sqlInnerWhere += "AND ( cs1.global_subscribed = 1 ) ";
      if ( useDateSend ) {
        sqlInnerWhere += "AND ( cs1.date_send IS NOT NULL ) ";
        sqlInnerWhere += "AND ( cs1.date_send > '" + dateSendMin + "' ) ";
      }
      sqlInnerWhere += "AND ( cs1.subscriber_group_id = " + sgId + " ) ";
      String sqlInnerGroup = "GROUP BY cs1.encrypt_phone ";
      String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
          + sqlInnerGroup;

      String sqlSelect = "SELECT COUNT(cs.id) as 'total' ";
      String sqlFrom = "FROM client_subscriber cs ";
      sqlFrom += "INNER JOIN ( " + sqlInner + " ) cs2 ON cs2.id = cs.id ";
      sql = sqlSelect + sqlFrom;

    }

    DLog.debug( lctx , "Perform " + sql );

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return total;
    }

    Iterator iter = qr.iterator();
    if ( !iter.hasNext() ) {
      return total;
    }

    QueryItem qi = (QueryItem) iter.next();
    if ( qi == null ) {
      return total;
    }

    String stemp = qi.getFirstValue();
    if ( ( stemp == null ) || ( stemp.equals( "" ) ) ) {
      return total;
    }

    try {
      total = Long.parseLong( stemp );
    } catch ( NumberFormatException e ) {
    }

    return total;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String populateFields() {
    String fields = "id , client_id , subscriber_group_id ";
    fields += ", " + sqlDecryptPhoneNumber( null , true );
    fields += ", subscribed , date_subscribed ";
    fields += ", global_subscribed , date_global_subscribed ";
    fields += ", cust_ref_id , cust_ref_code , date_send , description ";
    fields += ", global_invalid , date_global_invalid ";
    fields += ", global_dnc , date_global_dnc , active ";
    return fields;
  }

  private ClientSubscriberBean populateRecord( QueryResult qr ) {
    ClientSubscriberBean bean = null;
    if ( qr == null ) {
      return bean;
    }
    Iterator iter = qr.getIterator();
    if ( !iter.hasNext() ) {
      return bean;
    }
    bean = populateRecord( (QueryItem) iter.next() );
    return bean;
  }

  private ClientSubscriberBean populateRecord( QueryItem qi ) {
    ClientSubscriberBean bean = null;
    if ( qi == null ) {
      return bean;
    }

    String stemp = null;

    bean = new ClientSubscriberBean();

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

    stemp = (String) qi.get( 2 ); // subscriber_group_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setSubscriberGroupId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 3 ); // phone
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setPhone( stemp );
    }

    stemp = (String) qi.get( 4 ); // subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setSubscribed( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 5 ); // date_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateSubscribed( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 6 ); // global_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setGlobalSubscribed( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 7 ); // date_global_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalSubscribed( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 8 ); // cust_ref_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustomerReferenceId( stemp );
    }

    stemp = (String) qi.get( 9 ); // cust_ref_code
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustomerReferenceCode( stemp );
    }

    stemp = (String) qi.get( 10 ); // date_send
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateSend( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 11 ); // description
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDescription( stemp );
    }

    stemp = (String) qi.get( 12 ); // global_invalid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setGlobalInvalid( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 13 ); // date_global_invalid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalInvalid( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 14 ); // global_dnc
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setGlobalInvalid( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 15 ); // date_global_dnc
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalInvalid( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 16 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setActive( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
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
    sb.append( StringEscapeUtils.escapeSql( phoneNumber ) );
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
