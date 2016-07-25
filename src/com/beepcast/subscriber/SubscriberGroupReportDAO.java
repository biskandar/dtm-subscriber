package com.beepcast.subscriber;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.common.ClientCommon;
import com.beepcast.dbmanager.common.ClientCountriesCommon;
import com.beepcast.dbmanager.common.CountryCommon;
import com.beepcast.dbmanager.table.TClient;
import com.beepcast.dbmanager.table.TClientToCountries;
import com.beepcast.dbmanager.table.TClientToCountry;
import com.beepcast.dbmanager.table.TCountry;
import com.beepcast.encrypt.EncryptApp;
import com.beepcast.subscriber.common.DateTimeUtils;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupReportDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberGroupReportDAO" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private DatabaseLibrary dbLib;
  private final String keyPhoneNumber;
  private SubscriberGroupReportParamService sgrpService;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportDAO( SubscriberGroupReportParamService sgrpService ) {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );

    this.sgrpService = sgrpService;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupReportBean getReportBean( int subscriberGroupId ,
      boolean debug ) {
    SubscriberGroupReportBean reportBean = null;

    if ( subscriberGroupId < 1 ) {
      return reportBean;
    }

    // compose sql

    String sqlSelect = "SELECT id , subscriber_group_id , total_uploaded ";
    sqlSelect += ", total_subscribed , total_duplicated_number ";
    sqlSelect += ", total_invalid_number , active ";
    String sqlFrom = "FROM subscriber_group_report ";
    String sqlWhere = "WHERE ( active = 1 ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return reportBean;
    }

    // retrieve record

    QueryItem qi = null;
    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }

    // compose bean

    reportBean = SubscriberGroupReportFactory
        .createSubscriberGroupReportBean( subscriberGroupId );

    String stemp;

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // subscriber_group_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setSubscriberGroupId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // total_uploaded
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setTotalUploaded( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 3 ); // total_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setTotalSubscribed( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 4 ); // total_duplicated_number
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setTotalDuplicatedNumber( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 5 ); // total_invalid_number
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        reportBean.setTotalInvalidNumber( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 6 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      reportBean.setActive( stemp.equals( "1" ) );
    }

    return reportBean;
  }

  public boolean insertReportBean( SubscriberGroupReportBean sgrBean ,
      boolean debug ) {
    boolean result = false;

    if ( sgrBean == null ) {
      return result;
    }
    if ( sgrBean.getSubscriberGroupId() < 1 ) {
      return result;
    }

    // compose sql

    String sqlInsert = "INSERT INTO subscriber_group_report ";
    sqlInsert += "(subscriber_group_id,total_uploaded";
    sqlInsert += ",total_subscribed,total_duplicated_number";
    sqlInsert += ",total_invalid_number,active,date_inserted";
    sqlInsert += ",date_updated) ";
    String sqlValues = "VALUES (" + sgrBean.getSubscriberGroupId() + ","
        + sgrBean.getTotalUploaded() + "," + sgrBean.getTotalSubscribed() + ","
        + sgrBean.getTotalDuplicatedNumber() + ","
        + sgrBean.getTotalInvalidNumber() + "," + ( sgrBean.isActive() ? 1 : 0 )
        + ",NOW(),NOW()) ";

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

  public boolean updateReportBean( SubscriberGroupReportBean sgrBean ,
      boolean debug ) {
    boolean result = false;

    if ( sgrBean == null ) {
      return result;
    }
    if ( sgrBean.getId() < 1 ) {
      return result;
    }

    // compose sql
    String sqlUpdate = "UPDATE subscriber_group_report ";
    String sqlSet = "SET total_uploaded = " + sgrBean.getTotalUploaded()
        + " , total_subscribed = " + sgrBean.getTotalSubscribed()
        + " , total_duplicated_number = " + sgrBean.getTotalDuplicatedNumber()
        + " , total_invalid_number = " + sgrBean.getTotalInvalidNumber()
        + " , active = " + ( sgrBean.isActive() ? 1 : 0 )
        + " , date_updated = NOW() ";
    String sqlWhere = "WHERE id = " + sgrBean.getId() + " ";

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

  public SubscriberGroupReportBean synchReport( SubscriberGroupBean sgBean ,
      boolean debug ) {
    SubscriberGroupReportBean sgrBean = null;

    if ( sgBean == null ) {
      return sgrBean;
    }

    int sgId = sgBean.getId();
    if ( sgId < 1 ) {
      return sgrBean;
    }

    String headerLog = SubscriberGroupReportCommon.headerLog( sgId );

    try {

      // create report bean
      sgrBean = SubscriberGroupReportFactory
          .createSubscriberGroupReportBean( sgId );

      // setup summary report 1 : total all numbers
      if ( !setupSummaryReport1( sgBean , sgrBean , debug ) ) {
        DLog.warning( lctx , headerLog + "Failed to synch report "
            + ", failed to setup summary report of total numbers" );
        return sgrBean;
      }

      // update summary report 1 : total duplicated numbers
      sgrBean.setTotalDuplicatedNumber( totalDuplicatedNumbers(
          sgrBean.getSubscriberGroupId() , debug ) );

      // persist summary report 1 : total all numbers
      if ( !persistSummaryReport1( sgrBean , debug ) ) {
        if ( debug ) {
          DLog.warning( lctx , headerLog + "Failed to synch report "
              + ", failed to persist summary report of total numbers" );
        }
        return sgrBean;
      }

      // setup summary report 2 : total group numbers per country
      if ( !setupSummaryReport2( sgBean , sgrBean , debug ) ) {
        DLog.warning( lctx , headerLog + "Failed to synch report "
            + ", failed to setup summary report of group numbers" );
        return sgrBean;
      }

      // persist summary report 2 : all params
      if ( !persistSummaryReport2( sgrBean , debug ) ) {
        if ( debug ) {
          DLog.warning( lctx , headerLog + "Failed to synch report "
              + ", failed to persist summary report params" );
        }
        return sgrBean;
      }

      if ( debug ) {
        DLog.debug( lctx , headerLog + "Successfully performed synch report" );
      }

    } catch ( Exception e ) {
      DLog.warning( lctx , headerLog + "Failed to perform synch report , " + e );
    }

    return sgrBean;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean setupSummaryReport1( SubscriberGroupBean sgBean ,
      SubscriberGroupReportBean sgrBean , boolean debug ) {
    boolean result = false;

    if ( sgBean == null ) {
      return result;
    }
    if ( sgrBean == null ) {
      return result;
    }

    String headerLog = SubscriberGroupReportCommon.headerLog( sgrBean );

    // compose sql

    String sqlFieldTotalUpload = "COUNT( cs.encrypt_phone ) ";
    sqlFieldTotalUpload += "AS total_uploaded ";

    String sqlFieldTotalSubscribed = "SUM( IF( ( cs.subscribed = 1 ) ";
    sqlFieldTotalSubscribed += "AND ( cs.global_subscribed = 1 ) ";
    sqlFieldTotalSubscribed += "AND ( cs.global_invalid = 0 ) ";
    sqlFieldTotalSubscribed += "AND ( cs.global_dnc = 0 ) ";
    sqlFieldTotalSubscribed += "AND ( cs.active = 1 ) , 1 , 0 ) ) ";
    sqlFieldTotalSubscribed += "AS total_subscribed ";

    String sqlFieldTotalInvalid = "SUM( IF( ( cs.active = 0 ) ";
    sqlFieldTotalInvalid += "OR ( cs.global_invalid = 1 ) , 1 , 0 ) ) ";
    sqlFieldTotalInvalid += "AS total_invalid ";

    String sqlSelect = "SELECT " + sqlFieldTotalUpload + ", "
        + sqlFieldTotalSubscribed + ", " + sqlFieldTotalInvalid;
    String sqlFrom = "FROM client_subscriber cs ";
    String sqlWhere = "WHERE ( cs.subscriber_group_id = "
        + sgrBean.getSubscriberGroupId() + " ) ";

    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , headerLog + "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return result;
    }

    // retrieve record

    QueryItem qi = null;
    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }

    // update record

    String stemp;

    stemp = (String) qi.get( 0 ); // total_uploaded
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        sgrBean.setTotalUploaded( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // total_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        sgrBean.setTotalSubscribed( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // total_invalid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        sgrBean.setTotalInvalidNumber( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    if ( debug ) {
      DLog.debug( lctx , headerLog + "Found as the latest report summary "
          + sgrBean );
    }

    result = true;
    return result;
  }

  private boolean setupSummaryReport2( SubscriberGroupBean sgBean ,
      SubscriberGroupReportBean sgrBean , boolean debug ) {
    boolean result = false;

    if ( sgBean == null ) {
      return result;
    }
    if ( sgrBean == null ) {
      return result;
    }

    String headerLog = SubscriberGroupReportCommon.headerLog( sgrBean );

    // get client profile

    TClient clientBean = ClientCommon.getClient( sgBean.getClientId() );
    if ( clientBean == null ) {
      DLog.warning( lctx , headerLog + "Found empty / null client profile" );
      return result;
    }

    // define list of support countries

    TClientToCountries clientCountriesBean = ClientCountriesCommon
        .getClientToCountries( clientBean.getClientId() );
    if ( clientCountriesBean == null ) {
      DLog.warning( lctx , headerLog + "Found empty client countries support" );
      return result;
    }

    List listCountryIds = clientCountriesBean.getIds();
    if ( ( listCountryIds == null ) || ( listCountryIds.size() < 1 ) ) {
      DLog.warning( lctx , headerLog + "Found empty list "
          + "client countries support" );
      return result;
    }

    // switch default value as true

    result = true;

    // calculate total number based on country's phone prefix

    Iterator iterCountryIds = listCountryIds.iterator();
    while ( iterCountryIds.hasNext() ) {

      TClientToCountry clientCountryBean = clientCountriesBean
          .getClientToCountry( (String) iterCountryIds.next() );
      if ( clientCountryBean == null ) {
        continue;
      }

      TCountry countryBean = CountryCommon.getCountry( clientCountryBean
          .getCountryId() );
      if ( ( countryBean == null ) || ( !countryBean.isActive() ) ) {
        continue;
      }

      int subscriberGroupId = sgrBean.getSubscriberGroupId();
      String countryPhonePrefix = countryBean.getPhonePrefix();
      String countryCode = countryBean.getCode();
      if ( subscriberGroupId < 1 ) {
        continue;
      }
      if ( StringUtils.isBlank( countryPhonePrefix ) ) {
        continue;
      }
      if ( StringUtils.isBlank( countryCode ) ) {
        continue;
      }

      if ( !countryPhonePrefix.startsWith( "+" ) ) {
        countryPhonePrefix = "+" + countryPhonePrefix;
      }

      if ( setupParamTotalAllNumbers( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug ) < 1 ) {
        // found empty records , no need to process the rest
        continue;
      }
      setupParamTotalSubscribedNumbers( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );
      setupParamTotalUniqueNumbers( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );
      setupParamTotalSubscribedUniqueNumbers( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );

      if ( setupParamTotalAllScheduled( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug ) < 1 ) {
        // found empty records , no need to process the rest
        continue;
      }
      setupParamTotalSubscribedScheduled( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );
      setupParamTotalUniqueScheduled( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );
      setupParamTotalSubscribedUniqueScheduled( sgrBean , subscriberGroupId ,
          countryPhonePrefix , countryCode , debug );

    }

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////

  private int setupParamTotalAllNumbers( SubscriberGroupReportBean sgrBean ,
      int subscriberGroupId , String countryPhonePrefix , String countryCode ,
      boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // compose sql

    String sqlSelect = "SELECT COUNT(c.id) AS total ";
    String sqlFrom = "FROM client_subscriber c ";
    String sqlWhere = "WHERE ( c.subscriber_group_id = " + subscriberGroupId
        + " ) ";
    sqlWhere += "AND ( " + sqlDecryptPhoneNumber( "c" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport.addParamCountryTotalAllNumbers(
        sgrBean , countryCode , totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total all numbers : countryPhonePrefix = "
            + countryPhonePrefix + " ,  countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalSubscribedNumbers(
      SubscriberGroupReportBean sgrBean , int subscriberGroupId ,
      String countryPhonePrefix , String countryCode , boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // compose sql

    String sqlSelect = "SELECT COUNT(c.id) AS total ";
    String sqlFrom = "FROM client_subscriber c ";
    String sqlWhere = "WHERE ( c.subscriber_group_id = " + subscriberGroupId
        + " ) ";
    sqlWhere += "AND ( " + sqlDecryptPhoneNumber( "c" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    sqlWhere += "AND ( c.subscribed = 1 ) AND ( c.global_subscribed = 1 ) ";
    sqlWhere += "AND ( c.global_invalid = 0 ) AND ( c.global_dnc = 0 ) ";
    sqlWhere += "AND ( c.active = 1 ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport
        .addParamCountryTotalSubscribedNumbers( sgrBean , countryCode ,
            totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total subscribed numbers : countryPhonePrefix = "
            + countryPhonePrefix + " , countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalUniqueNumbers( SubscriberGroupReportBean sgrBean ,
      int subscriberGroupId , String countryPhonePrefix , String countryCode ,
      boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // compose sql

    String sqlInnerSelect = "SELECT MAX(cs1.id) AS id ";
    String sqlInnerFrom = "FROM client_subscriber cs1 ";
    String sqlInnerWhere = "WHERE ( cs1.subscriber_group_id = "
        + subscriberGroupId + " ) ";
    sqlInnerWhere += "AND ( " + sqlDecryptPhoneNumber( "cs1" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    String sqlInnerGroup = "GROUP BY cs1.encrypt_phone ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup;

    String sqlSelect = "SELECT COUNT(cs.id) AS total ";
    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "INNER JOIN ( " + sqlInner + " ) cs2 ON cs2.id = cs.id ";
    String sql = sqlSelect + sqlFrom;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport.addParamCountryTotalUniqueNumbers(
        sgrBean , countryCode , totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total unique numbers : countryPhonePrefix = "
            + countryPhonePrefix + " , countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalSubscribedUniqueNumbers(
      SubscriberGroupReportBean sgrBean , int subscriberGroupId ,
      String countryPhonePrefix , String countryCode , boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // compose sql

    String sqlInnerSelect = "SELECT MAX(cs1.id) AS id ";
    String sqlInnerFrom = "FROM client_subscriber cs1 ";
    String sqlInnerWhere = "WHERE ( cs1.subscriber_group_id = "
        + subscriberGroupId + " ) ";
    sqlInnerWhere += "AND ( " + sqlDecryptPhoneNumber( "cs1" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    sqlInnerWhere += "AND ( cs1.subscribed = 1 ) AND ( cs1.global_subscribed = 1 ) ";
    sqlInnerWhere += "AND ( cs1.global_invalid = 0 ) AND ( cs1.global_dnc = 0 ) ";
    sqlInnerWhere += "AND ( cs1.active = 1 ) ";
    String sqlInnerGroup = "GROUP BY cs1.encrypt_phone ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup;

    String sqlSelect = "SELECT COUNT(cs.id) AS total ";
    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "INNER JOIN ( " + sqlInner + " ) cs2 ON cs2.id = cs.id ";
    String sql = sqlSelect + sqlFrom;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport
        .addParamCountryTotalSubscribedUniqueNumbers( sgrBean , countryCode ,
            totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total subscribed unique numbers : "
            + "countryPhonePrefix = " + countryPhonePrefix
            + " , countryCode = " + countryCode + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////

  private int setupParamTotalAllScheduled( SubscriberGroupReportBean sgrBean ,
      int subscriberGroupId , String countryPhonePrefix , String countryCode ,
      boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // prepared

    String dateSendMin = DateTimeUtils.sqlDtmDaysBefore( 0 );

    // compose sql

    String sqlSelect = "SELECT COUNT(c.id) AS total ";
    String sqlFrom = "FROM client_subscriber c ";
    String sqlWhere = "WHERE ( c.subscriber_group_id = " + subscriberGroupId
        + " ) ";
    sqlWhere += "AND ( c.date_send IS NOT NULL ) AND ( c.date_send > '"
        + dateSendMin + "' ) ";
    sqlWhere += "AND ( " + sqlDecryptPhoneNumber( "c" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport.addParamCountryTotalAllScheduled(
        sgrBean , countryCode , totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total all scheduled : countryPhonePrefix = "
            + countryPhonePrefix + " ,  countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalSubscribedScheduled(
      SubscriberGroupReportBean sgrBean , int subscriberGroupId ,
      String countryPhonePrefix , String countryCode , boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // prepared

    String dateSendMin = DateTimeUtils.sqlDtmDaysBefore( 0 );

    // compose sql

    String sqlSelect = "SELECT COUNT(c.id) AS total ";
    String sqlFrom = "FROM client_subscriber c ";
    String sqlWhere = "WHERE ( c.subscriber_group_id = " + subscriberGroupId
        + " ) ";
    sqlWhere += "AND ( c.date_send IS NOT NULL ) AND ( c.date_send > '"
        + dateSendMin + "' ) ";
    sqlWhere += "AND ( " + sqlDecryptPhoneNumber( "c" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    sqlWhere += "AND ( c.subscribed = 1 ) AND ( c.global_subscribed = 1 ) ";
    sqlWhere += "AND ( c.global_invalid = 0 ) AND ( c.global_dnc = 0 ) ";
    sqlWhere += "AND ( c.active = 1 ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport
        .addParamCountryTotalSubscribedScheduled( sgrBean , countryCode ,
            totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total subscribed scheduled : countryPhonePrefix = "
            + countryPhonePrefix + " , countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalUniqueScheduled(
      SubscriberGroupReportBean sgrBean , int subscriberGroupId ,
      String countryPhonePrefix , String countryCode , boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // prepared

    String dateSendMin = DateTimeUtils.sqlDtmDaysBefore( 0 );

    // compose sql

    String sqlInnerSelect = "SELECT MAX(cs1.id) AS id ";
    String sqlInnerFrom = "FROM client_subscriber cs1 ";
    String sqlInnerWhere = "WHERE ( cs1.subscriber_group_id = "
        + subscriberGroupId + " ) ";
    sqlInnerWhere += "AND ( cs1.date_send IS NOT NULL ) AND ( cs1.date_send > '"
        + dateSendMin + "' ) ";
    sqlInnerWhere += "AND ( " + sqlDecryptPhoneNumber( "cs1" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    String sqlInnerGroup = "GROUP BY cs1.encrypt_phone ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup;

    String sqlSelect = "SELECT COUNT(cs.id) AS total ";
    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "INNER JOIN ( " + sqlInner + " ) cs2 ON cs2.id = cs.id ";
    String sql = sqlSelect + sqlFrom;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport.addParamCountryTotalUniqueScheduled(
        sgrBean , countryCode , totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total unique scheduled : countryPhonePrefix = "
            + countryPhonePrefix + " , countryCode = " + countryCode
            + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  private int setupParamTotalSubscribedUniqueScheduled(
      SubscriberGroupReportBean sgrBean , int subscriberGroupId ,
      String countryPhonePrefix , String countryCode , boolean debug ) {
    int totalRecords = 0;

    String headerLog = SubscriberGroupReportCommon
        .headerLog( subscriberGroupId );

    // prepared

    String dateSendMin = DateTimeUtils.sqlDtmDaysBefore( 0 );

    // compose sql

    String sqlInnerSelect = "SELECT MAX(cs1.id) AS id ";
    String sqlInnerFrom = "FROM client_subscriber cs1 ";
    String sqlInnerWhere = "WHERE ( cs1.subscriber_group_id = "
        + subscriberGroupId + " ) ";
    sqlInnerWhere += "AND ( cs1.date_send IS NOT NULL ) AND ( cs1.date_send > '"
        + dateSendMin + "' ) ";
    sqlInnerWhere += "AND ( " + sqlDecryptPhoneNumber( "cs1" ) + " LIKE '"
        + countryPhonePrefix + "%' ) ";
    sqlInnerWhere += "AND ( cs1.subscribed = 1 ) AND ( cs1.global_subscribed = 1 ) ";
    sqlInnerWhere += "AND ( cs1.global_invalid = 0 ) AND ( cs1.global_dnc = 0 ) ";
    sqlInnerWhere += "AND ( cs1.active = 1 ) ";
    String sqlInnerGroup = "GROUP BY cs1.encrypt_phone ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup;

    String sqlSelect = "SELECT COUNT(cs.id) AS total ";
    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "INNER JOIN ( " + sqlInner + " ) cs2 ON cs2.id = cs.id ";
    String sql = sqlSelect + sqlFrom;

    // execute sql

    totalRecords = readTotalRecords( sql , debug , headerLog );

    // store into table

    if ( SubscriberGroupReportParamSupport
        .addParamCountryTotalSubscribedUniqueScheduled( sgrBean , countryCode ,
            totalRecords ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Added param total subscribed unique scheduled : "
            + "countryPhonePrefix = " + countryPhonePrefix
            + " , countryCode = " + countryCode + " , total = " + totalRecords );
      }
    }

    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////

  private int readTotalRecords( String sql , boolean debug , String headerLog ) {
    int totalRecords = 0;

    if ( debug ) {
      DLog.debug( lctx , headerLog + "Perform " + sql );
    }

    // execute sql

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalRecords;
    }

    // read record

    Iterator iter = qr.iterator();
    if ( !iter.hasNext() ) {
      return totalRecords;

    }
    QueryItem qi = (QueryItem) iter.next();
    if ( qi == null ) {
      return totalRecords;
    }

    // parse record value into integer

    try {
      totalRecords = Integer.parseInt( qi.getFirstValue() );
    } catch ( NumberFormatException e ) {
    }

    return totalRecords;
  }

  private boolean persistSummaryReport1( SubscriberGroupReportBean sgrBean ,
      boolean debug ) {
    boolean result = false;

    if ( sgrBean == null ) {
      return result;
    }

    String headerLog = SubscriberGroupReportCommon.headerLog( sgrBean );

    SubscriberGroupReportBean sgrBeanOri = getReportBean(
        sgrBean.getSubscriberGroupId() , debug );

    String cmd = "";

    if ( sgrBeanOri == null ) {
      cmd = "insert";
    } else {
      if ( sgrBeanOri.getId() < 1 ) {
        cmd = "insert";
      } else {
        sgrBean.setId( sgrBeanOri.getId() );
        if ( ( sgrBeanOri.getTotalUploaded() != sgrBean.getTotalUploaded() )
            || ( sgrBeanOri.getTotalSubscribed() != sgrBean
                .getTotalSubscribed() )
            || ( sgrBeanOri.getTotalDuplicatedNumber() != sgrBean
                .getTotalDuplicatedNumber() )
            || ( sgrBeanOri.getTotalInvalidNumber() != sgrBean
                .getTotalInvalidNumber() ) ) {
          cmd = "update";
        }
      }
    }

    if ( StringUtils.equalsIgnoreCase( cmd , "insert" ) ) {
      DLog.debug( lctx , headerLog + "Found empty subscriber group report "
          + ", will insert " + sgrBean );
      result = insertReportBean( sgrBean , debug );
      if ( result ) {
        sgrBeanOri = getReportBean( sgrBean.getSubscriberGroupId() , debug );
        if ( sgrBeanOri != null ) {
          sgrBean.setId( sgrBeanOri.getId() );
        }
      } else {
        DLog.warning( lctx , headerLog + "Failed to insert new record "
            + "of subscriber group report " );
      }
      result = true;
    }

    if ( StringUtils.equalsIgnoreCase( cmd , "update" ) ) {
      DLog.debug( lctx , headerLog + "Update subscriber group report "
          + ", with " + sgrBean );
      result = updateReportBean( sgrBean , debug );
      if ( !result ) {
        DLog.warning( lctx , headerLog + "Failed to update a record "
            + "of subscriber group report " );
      }
      result = true;
    }

    if ( StringUtils.isBlank( cmd ) ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog + "There is no change inside the "
            + "total subscriber group summary to be made" );
      }
    }

    return result;
  }

  private boolean persistSummaryReport2( SubscriberGroupReportBean sgrBean ,
      boolean debug ) {
    boolean result = false;

    if ( sgrBean == null ) {
      return result;
    }

    int sgId = sgrBean.getSubscriberGroupId();
    if ( sgId < 1 ) {
      return result;
    }

    List listFields = sgrBean.getParamFields();
    if ( listFields == null ) {
      return result;
    }

    // first , set all to inactive
    sgrpService.updateActiveBeanBySubscriberGroupId( sgId , false );

    Iterator iterFields = listFields.iterator();
    while ( iterFields.hasNext() ) {
      String field = (String) iterFields.next();
      if ( StringUtils.isBlank( field ) ) {
        continue;
      }
      String value = sgrBean.getParamValue( field );
      if ( StringUtils.isBlank( value ) ) {
        continue;
      }
      SubscriberGroupReportParamBean sgrpBean = SubscriberGroupReportParamFactory
          .createSubscriberGroupReportParamBean( sgId , field , value );
      if ( sgrpBean == null ) {
        continue;
      }
      sgrpService.insertBean( sgrpBean );
    }

    result = true;
    return result;
  }

  private int totalDuplicatedNumbers( int subscriberGroupId , boolean debug ) {
    int totalNumbers = 0;

    if ( subscriberGroupId < 1 ) {
      return totalNumbers;
    }

    // compose sql

    String sqlInnerSelect = "SELECT c.encrypt_phone ";
    sqlInnerSelect += ", COUNT( c.encrypt_phone ) AS duplicated_numbers ";
    String sqlInnerFrom = "FROM client_subscriber c ";
    String sqlInnerWhere = "WHERE ( c.subscriber_group_id = "
        + subscriberGroupId + " ) ";
    String sqlInnerGroup = "GROUP BY c.encrypt_phone ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup;

    String sqlSelect = "SELECT COUNT( t.duplicated_numbers ) AS total_duplicated ";
    String sqlFrom = "FROM ( " + sqlInner + " ) t ";
    String sqlWhere = "WHERE ( t.duplicated_numbers > 1 ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // execute sql

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalNumbers;
    }
    QueryItem qi = null;
    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }
    if ( qi == null ) {
      return totalNumbers;
    }

    try {
      totalNumbers = Integer.parseInt( qi.getFirstValue() );
    } catch ( NumberFormatException e ) {
      return totalNumbers;
    }

    return totalNumbers;
  }

  private String sqlDecryptPhoneNumber( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" );
    sb.append( tableAlias );
    sb.append( ".encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "')" );
    return sb.toString();
  }

}
