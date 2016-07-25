package com.beepcast.subscriber;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.ConnectionWrapper;
import com.beepcast.database.DatabaseLibrary;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.mysql.jdbc.CommunicationsException;

public class ClientSubscriberFileService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberFileService" );

  static final String STATUS_NUMBER_SUBSCRIBED = "true";
  static final String STATUS_NUMBER_UNSUBSCRIBED = "false";
  static final String STATUS_NUMBER_VALID = "true";
  static final String STATUS_NUMBER_INVALID = "false";
  static final String STATUS_NUMBER_REGISTERED = "true";
  static final String STATUS_NUMBER_UNREGISTERED = "false";

  static final int BUFFER = 2048;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private DatabaseLibrary dbLib;

  private final String keyPhoneNumber;
  private final String keyIc;
  private final String keyLastName;
  private final String keyName;
  private final String keyEmail;
  private final String keyCustom;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberFileService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
    keyIc = encryptApp.getKeyValue( EncryptApp.KEYNAME_USERID );
    keyLastName = encryptApp.getKeyValue( EncryptApp.KEYNAME_USERNAME );
    keyName = encryptApp.getKeyValue( EncryptApp.KEYNAME_USERNAME );
    keyEmail = encryptApp.getKeyValue( EncryptApp.KEYNAME_EMAIL );
    keyCustom = encryptApp.getKeyValue( EncryptApp.KEYNAME_CUSTOM );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String filePathWithoutExtension( int subscriberGroupId ,
      Boolean subscribedStatus , String channel ) {
    String strFileName = null;

    // make sure the app is ready
    if ( subscriberApp == null ) {
      return strFileName;
    }

    // get group subscriber name
    String sgName = Integer.toString( subscriberGroupId );
    if ( subscriberGroupId > 0 ) {
      SubscriberGroupBean sgBean = subscriberApp
          .getSubscriberGroupBean( subscriberGroupId );
      if ( sgBean != null ) {
        sgName = sgBean.getGroupName();
      }
    }

    // verify group subscriber name
    if ( StringUtils.isBlank( sgName ) ) {
      DLog.warning( lctx , "Failed to generate file path "
          + ", found blank subscriber group name" );
      return strFileName;
    }

    // clean characters
    sgName = sgName.trim();
    sgName = sgName.replaceAll( "[^a-zA-Z0-9]" , "_" );

    // subscribed status
    String sgStatus = channel;
    if ( StringUtils.isBlank( sgStatus ) ) {
      sgStatus = "all";
      if ( subscribedStatus != null ) {
        sgStatus = ( subscribedStatus.booleanValue() ) ? "subscribed"
            : "unsubscribed";
      }
    }

    // add date created time
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd.HHmmss" );
    String dateTimeNowStr = sdf.format( now );

    // random alphanumeric 5 digit
    String randAlphanum5digits = RandomStringUtils.randomAlphanumeric( 5 );

    // compose str file name
    strFileName = "list-".concat( sgName ).concat( "-" ).concat( sgStatus )
        .concat( "-" ).concat( dateTimeNowStr ).concat( "-" )
        .concat( randAlphanum5digits );

    return strFileName;
  }

  public boolean generateListOfAllNumbers( String filePathWithoutExtension ,
      int groupSubscriberId , Boolean subscribedStatus , ArrayList listAddFields ) {
    boolean result = false;

    if ( groupSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to generate file of numbers "
          + ", found zero groupSubscriberId" );
      return result;
    }

    String headerLog = SubscriberGroupCommon.headerLog( groupSubscriberId );

    if ( StringUtils.isBlank( filePathWithoutExtension ) ) {
      DLog.warning( lctx , headerLog + "Failed to generate file of numbers "
          + ", found empty filePathWithoutExtension" );
      return result;
    }

    String sql = sqlSelectFromWhere( groupSubscriberId , subscribedStatus );

    ArrayList listFields = ClientSubscriberFileHeaders.initFileHeaders();
    if ( listAddFields != null ) {
      listFields.addAll( listAddFields );
    }

    DLog.debug( lctx , headerLog
        + "Generating file list of all numbers with fields : " + listFields );

    result = extractNumbersToFile( subscriberApp.isDebug() , headerLog ,
        filePathWithoutExtension , sql , listFields );

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlSelectFromWhere( int groupSubscriberId ,
      Boolean subscribedStatus ) {

    String sqlSelect = "SELECT " + sqlDecryptPhoneNumber( "cs" ) + " ";

    sqlSelect += ", cs.date_inserted ";
    sqlSelect += ", cs.subscribed , cs.date_subscribed ";
    sqlSelect += ", cs.global_subscribed , cs.date_global_subscribed ";
    sqlSelect += ", cs.cust_ref_id , cs.cust_ref_code , cs.date_send ";
    sqlSelect += ", cs.description , cs.global_invalid , cs.date_global_invalid ";
    sqlSelect += ", cs.global_dnc , cs.date_global_dnc ";

    sqlSelect += ", cs.subscribed_app_id ";
    sqlSelect += ", csu.from_app_id ";

    sqlSelect += ", e1.event_name AS cs_event_name ";
    sqlSelect += ", e2.event_name AS csu_event_name ";
    sqlSelect += ", e3.event_name AS csi_event_name ";

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

    sqlSelect += ", " + sqlDecryptName( "mu" ) + " , "
        + sqlDecryptLastName( "mu" ) + " , " + sqlDecryptEmail( "mu" )
        + " , mu.nationality , " + sqlDecryptIc( "mu" ) + " , mu.gender ";

    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "LEFT OUTER JOIN client_subscriber_unsubs csu ON "
        + "( csu.client_id = cs.client_id ) AND ( csu.encrypt_phone = cs.encrypt_phone ) AND ( csu.active = 1 ) ";
    sqlFrom += "LEFT OUTER JOIN client_subscriber_invalid csi ON "
        + "( csi.client_id = cs.client_id ) AND ( csi.encrypt_phone = cs.encrypt_phone ) AND ( csi.active = 1 ) ";
    sqlFrom += "LEFT OUTER JOIN client_subscriber_custom csc ON "
        + "( cs.id = csc.client_subscriber_id ) ";
    sqlFrom += "LEFT OUTER JOIN mobile_user mu ON "
        + "( mu.client_id = cs.client_id ) AND ( mu.encrypt_phone = cs.encrypt_phone ) ";
    sqlFrom += "LEFT OUTER JOIN event e1 ON "
        + "( e1.event_id = cs.subscribed_event_id ) ";
    sqlFrom += "LEFT OUTER JOIN event e2 ON "
        + "( e2.event_id = csu.from_event_id ) ";
    sqlFrom += "LEFT OUTER JOIN event e3 ON "
        + "( e3.event_id = csi.from_event_id ) ";

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
    // String sqlOrder = "ORDER BY " + sqlDecryptPhoneNumber( false ) + " ASC ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

    return sql;
  }

  private boolean extractNumbersToFile( boolean debug , String headerLog ,
      String filePathWithoutExtension , String sql , ArrayList listFields ) {
    boolean result = false;

    // will calculate total records read
    int totalRecords = 0;

    try {

      // trap delta time
      long deltaTime = System.currentTimeMillis();

      // put txt extension
      String txtFileName = filePathWithoutExtension + ".csv";
      DLog.debug( lctx , headerLog + "Preparing to create text file = "
          + txtFileName );

      // prepare text file object
      File file = new File( txtFileName );
      FileOutputStream fos = new FileOutputStream( file );
      DataOutputStream dos = new DataOutputStream( fos );

      // create header
      extractHeaderToFile( dos , listFields );

      // prepare database connection
      String databaseName = "profiledb";
      ConnectionWrapper conn = dbLib.getReaderConnection( databaseName );
      if ( ( conn != null ) && ( conn.isConnected() ) ) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
          statement = conn.createStatement();
          if ( statement != null ) {
            if ( debug ) {
              DLog.debug( lctx , headerLog + "Perform " + sql );
            }
            resultSet = statement.executeQuery( sql );
          }
          if ( resultSet != null ) {
            while ( resultSet.next() ) {
              if ( extractNumberToFile( headerLog , dos , resultSet ,
                  listFields ) ) {
                totalRecords = totalRecords + 1;
              }
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
      // DLog.debug( lctx , headerLog + "Saved text file = " + txtFileName );
      dos.close();

      // generate to zip file

      String zipFilename = "";
      zipFilename += FilenameUtils.removeExtension( file.getAbsolutePath() );
      zipFilename += ".zip";

      // DLog.debug( lctx , headerLog +"Converting text to zip file = " +
      // zipFilename );

      FileOutputStream dst = new FileOutputStream( zipFilename );
      CheckedOutputStream cos = new CheckedOutputStream( dst , new Adler32() );
      ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream( cos ) );

      byte data[] = new byte[BUFFER];
      FileInputStream fis = new FileInputStream( file );
      BufferedInputStream ori = new BufferedInputStream( fis , BUFFER );
      ZipEntry zipEntry = new ZipEntry( FilenameUtils.getName( file
          .getAbsolutePath() ) );
      zos.putNextEntry( zipEntry );
      int count = 0;
      while ( ( count = ori.read( data , 0 , BUFFER ) ) != -1 ) {
        zos.write( data , 0 , count );
      }
      ori.close();

      // save zip file
      // DLog.debug( lctx , headerLog + "Saved zip file = " + zipFilename );
      zos.close();

      // delete text file
      // DLog.debug( lctx , headerLog + "Delete text file = " + txtFileName );
      file.delete();

      // calculate delta time
      deltaTime = System.currentTimeMillis() - deltaTime;

      // log the summary
      DLog.debug( lctx , headerLog
          + "Successfully created file of client subscribers " + ": name = "
          + zipFilename + " , total = " + totalRecords + " record(s) , take = "
          + deltaTime + " ms" );

      // set result as true
      result = true;

    } catch ( Exception e ) {
      DLog.warning( lctx , headerLog + "Failed to generate report file , " + e );
    }

    return result;
  }

  private boolean extractHeaderToFile( DataOutputStream dos ,
      ArrayList listFields ) throws IOException {
    boolean result = false;
    if ( dos == null ) {
      return result;
    }
    if ( listFields == null ) {
      return result;
    }
    if ( listFields.size() < 1 ) {
      return result;
    }
    dos.writeBytes( StringUtils.join( listFields , "," ) );
    dos.writeBytes( "\n" );
    result = true;
    return result;
  }

  private boolean extractNumberToFile( String headerLog , DataOutputStream dos ,
      ResultSet resultSet , ArrayList listFields ) throws SQLException ,
      IOException {
    boolean result = false;

    if ( dos == null ) {
      DLog.warning( lctx , headerLog + "Failed to extract number to file "
          + ", found null dos" );
      return result;
    }
    if ( resultSet == null ) {
      DLog.warning( lctx , headerLog + "Failed to extract number to file "
          + ", found null result set" );
      return result;
    }
    if ( listFields == null ) {
      DLog.warning( lctx , headerLog + "Failed to extract number to file "
          + ", found null list fields" );
      return result;
    }
    int sizeFields = listFields.size();
    if ( sizeFields < 1 ) {
      DLog.warning( lctx , headerLog + "Failed to extract number to file "
          + ", found empty list fields" );
      return result;
    }

    String subscribedStatus = resultSet.getInt( "subscribed" ) > 0 ? STATUS_NUMBER_SUBSCRIBED
        : STATUS_NUMBER_UNSUBSCRIBED;
    String globalSubscribedStatus = resultSet.getInt( "global_subscribed" ) > 0 ? STATUS_NUMBER_SUBSCRIBED
        : STATUS_NUMBER_UNSUBSCRIBED;
    String globalValidStatus = resultSet.getInt( "global_invalid" ) < 1 ? STATUS_NUMBER_VALID
        : STATUS_NUMBER_INVALID;
    String notDncRegisteredStatus = resultSet.getInt( "global_dnc" ) < 1 ? STATUS_NUMBER_REGISTERED
        : STATUS_NUMBER_UNREGISTERED;

    StringBuffer cvsLines = null;
    for ( int idxField = 0 ; idxField < sizeFields ; idxField++ ) {
      String fieldName = (String) listFields.get( idxField );
      if ( StringUtils.isBlank( fieldName ) ) {
        continue;
      }
      Date dtemp = null;
      String stemp = null;

      if ( fieldName.equalsIgnoreCase( ClientSubscriberFileHeader.PHONENUMBER ) ) {
        stemp = resultSet.getString( "phone" );
        if ( ( stemp == null ) || ( stemp.equals( "" ) ) ) {
          continue;
        }
        if ( stemp.startsWith( "+" ) ) {
          stemp = stemp.substring( 1 );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.DATECREATED ) ) {
        stemp = resultSet.getString( "date_inserted" );
        dtemp = DateTimeFormat.convertToDate( stemp );
        stemp = DateTimeFormat.convertToString( dtemp );
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.DATEMODIFIED ) ) {
        stemp = resultSet.getString( "date_updated" );
        if ( ( stemp == null ) || ( stemp.equals( "" ) ) ) {
          stemp = resultSet.getString( "date_inserted" );
        }
        dtemp = DateTimeFormat.convertToDate( stemp );
        stemp = DateTimeFormat.convertToString( dtemp );
      } else if ( StringUtils.startsWithIgnoreCase( fieldName ,
          "CustomerReference" ) ) {
        if ( fieldName.equalsIgnoreCase( ClientSubscriberFileHeader.CUSTREFID ) ) {
          stemp = resultSet.getString( "cust_ref_id" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTREFCODE ) ) {
          stemp = resultSet.getString( "cust_ref_code" );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.DATESEND ) ) {
        stemp = resultSet.getString( "date_send" );
        dtemp = DateTimeFormat.convertToDate( stemp );
        stemp = DateTimeFormat.convertToString( dtemp );
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.SUBSCRIBEDSTATUS ) ) {
        stemp = subscribedStatus;
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.UNSUBSCRIBEDEVENTNAME ) ) {
        if ( StringUtils.equals( globalSubscribedStatus ,
            STATUS_NUMBER_UNSUBSCRIBED ) ) {
          stemp = StringUtils.trimToEmpty( resultSet
              .getString( "csu_event_name" ) );
          if ( StringUtils.isBlank( stemp ) ) {
            stemp = StringUtils.trimToEmpty( resultSet
                .getString( "from_app_id" ) );
          }
        }
        if ( StringUtils.equals( subscribedStatus , STATUS_NUMBER_UNSUBSCRIBED ) ) {
          stemp = StringUtils.trimToEmpty( resultSet
              .getString( "cs_event_name" ) );
          if ( StringUtils.isBlank( stemp ) ) {
            stemp = StringUtils.trimToEmpty( resultSet
                .getString( "subscribed_app_id" ) );
          }
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.UNSUBSCRIBEDDATE ) ) {
        if ( StringUtils.equals( subscribedStatus , STATUS_NUMBER_UNSUBSCRIBED ) ) {
          stemp = resultSet.getString( "date_subscribed" );
          dtemp = DateTimeFormat.convertToDate( stemp );
          stemp = DateTimeFormat.convertToString( dtemp );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GLOBALSUBSCRIBEDSTATUS ) ) {
        stemp = globalSubscribedStatus;
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GLOBALUNSUBSCRIBEDDATE ) ) {
        if ( StringUtils.equals( globalSubscribedStatus ,
            STATUS_NUMBER_UNSUBSCRIBED ) ) {
          stemp = resultSet.getString( "date_global_subscribed" );
          dtemp = DateTimeFormat.convertToDate( stemp );
          stemp = DateTimeFormat.convertToString( dtemp );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GLOBALVALIDSTATUS ) ) {
        stemp = globalValidStatus;
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GLOBALINVALIDDATE ) ) {
        if ( StringUtils.equals( globalValidStatus , STATUS_NUMBER_INVALID ) ) {
          stemp = resultSet.getString( "date_global_invalid" );
          dtemp = DateTimeFormat.convertToDate( stemp );
          stemp = DateTimeFormat.convertToString( dtemp );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GLOBALINVALIDEVENTNAME ) ) {
        if ( StringUtils.equals( globalValidStatus , STATUS_NUMBER_INVALID ) ) {
          stemp = StringUtils.trimToEmpty( resultSet
              .getString( "csi_event_name" ) );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.NOTDNCREGISTEREDSTATUS ) ) {
        stemp = notDncRegisteredStatus;
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.DNCREGISTEREDDATE ) ) {
        if ( StringUtils.equals( notDncRegisteredStatus ,
            STATUS_NUMBER_UNREGISTERED ) ) {
          stemp = resultSet.getString( "date_global_dnc" );
          dtemp = DateTimeFormat.convertToDate( stemp );
          stemp = DateTimeFormat.convertToString( dtemp );
        }
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.DESCRIPTION ) ) {
        stemp = resultSet.getString( "description" );
      } else if ( StringUtils.startsWithIgnoreCase( fieldName , "Custom" ) ) {

        if ( fieldName.equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM0 ) ) {
          stemp = resultSet.getString( "custom_0" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM1 ) ) {
          stemp = resultSet.getString( "custom_1" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM2 ) ) {
          stemp = resultSet.getString( "custom_2" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM3 ) ) {
          stemp = resultSet.getString( "custom_3" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM4 ) ) {
          stemp = resultSet.getString( "custom_4" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM5 ) ) {
          stemp = resultSet.getString( "custom_5" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM6 ) ) {
          stemp = resultSet.getString( "custom_6" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM7 ) ) {
          stemp = resultSet.getString( "custom_7" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM8 ) ) {
          stemp = resultSet.getString( "custom_8" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM9 ) ) {
          stemp = resultSet.getString( "custom_9" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM10 ) ) {
          stemp = resultSet.getString( "custom_10" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM11 ) ) {
          stemp = resultSet.getString( "custom_11" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM12 ) ) {
          stemp = resultSet.getString( "custom_12" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM13 ) ) {
          stemp = resultSet.getString( "custom_13" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM14 ) ) {
          stemp = resultSet.getString( "custom_14" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM15 ) ) {
          stemp = resultSet.getString( "custom_15" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM16 ) ) {
          stemp = resultSet.getString( "custom_16" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM17 ) ) {
          stemp = resultSet.getString( "custom_17" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM18 ) ) {
          stemp = resultSet.getString( "custom_18" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM19 ) ) {
          stemp = resultSet.getString( "custom_19" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM20 ) ) {
          stemp = resultSet.getString( "custom_20" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM21 ) ) {
          stemp = resultSet.getString( "custom_21" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM22 ) ) {
          stemp = resultSet.getString( "custom_22" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM23 ) ) {
          stemp = resultSet.getString( "custom_23" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM24 ) ) {
          stemp = resultSet.getString( "custom_24" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM25 ) ) {
          stemp = resultSet.getString( "custom_25" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM26 ) ) {
          stemp = resultSet.getString( "custom_26" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM27 ) ) {
          stemp = resultSet.getString( "custom_27" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM28 ) ) {
          stemp = resultSet.getString( "custom_28" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM29 ) ) {
          stemp = resultSet.getString( "custom_29" );
        } else if ( fieldName
            .equalsIgnoreCase( ClientSubscriberFileHeader.CUSTOM30 ) ) {
          stemp = resultSet.getString( "custom_30" );
        }

      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.FIRST_NAME ) ) {
        stemp = resultSet.getString( "name" );
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.FAMILY_NAME ) ) {
        stemp = resultSet.getString( "last_name" );
      } else if ( fieldName.equalsIgnoreCase( ClientSubscriberFileHeader.EMAIL ) ) {
        stemp = resultSet.getString( "email" );
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.NATIONALITY ) ) {
        stemp = resultSet.getString( "nationality" );
      } else if ( fieldName.equalsIgnoreCase( ClientSubscriberFileHeader.ID ) ) {
        stemp = resultSet.getString( "ic" );
      } else if ( fieldName
          .equalsIgnoreCase( ClientSubscriberFileHeader.GENDER ) ) {
        stemp = resultSet.getString( "gender" );
      }

      stemp = ( stemp == null ) ? "" : StringEscapeUtils.escapeCsv( stemp );
      if ( cvsLines == null ) {
        cvsLines = new StringBuffer();
      } else {
        cvsLines.append( "," );
      }
      cvsLines.append( stemp );
    }
    if ( cvsLines != null ) {
      dos.writeBytes( cvsLines.toString() );
      dos.writeBytes( "\n" );
    }

    result = true;
    return result;
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

  private String sqlDecryptIc( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_ic,'" );
    sb.append( keyIc );
    sb.append( "') AS ic" );
    return sb.toString();
  }

  private String sqlDecryptLastName( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_last_name,'" );
    sb.append( keyLastName );
    sb.append( "') AS last_name" );
    return sb.toString();
  }

  private String sqlDecryptName( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_name,'" );
    sb.append( keyName );
    sb.append( "') AS name" );
    return sb.toString();
  }

  private String sqlDecryptEmail( String tableAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" + tableAlias + ".encrypt_email,'" );
    sb.append( keyEmail );
    sb.append( "') AS email" );
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
