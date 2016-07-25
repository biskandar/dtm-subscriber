package com.beepcast.subscriber;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.beepcast.database.ConnectionWrapper;
import com.beepcast.database.DatabaseLibrary;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.mysql.jdbc.CommunicationsException;

public class ClientSubscriberDncFileService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberDncFileService" );

  static final int BUFFER = 2048;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private DatabaseLibrary dbLib;
  private String CanonicalDateTimeFormat;
  private final String keyPhoneNumber;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberDncFileService( SubscriberApp subscriberApp ) {
    if ( subscriberApp == null ) {
      subscriberApp = SubscriberApp.getInstance();
    }
    this.subscriberApp = subscriberApp;
    dbLib = DatabaseLibrary.getInstance();
    CanonicalDateTimeFormat = "dd MMM yyyy HH:mm";

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String filePathWithoutExtension() {
    String strFileName = null;

    // add date created time
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd.HHmmss" );
    String dateTimeNowStr = sdf.format( now );

    // random alphanumeric 5 digit
    String randAlphanum5digits = RandomStringUtils.randomAlphanumeric( 5 );

    // compose str file name
    strFileName = "dnc-numbers-".concat( dateTimeNowStr ).concat( "-" )
        .concat( randAlphanum5digits );

    return strFileName;
  }

  public boolean generateListOfAllNumbers( String filePathWithoutExtension ,
      int clientId ) {
    boolean result = false;

    if ( clientId < 1 ) {
      DLog.warning( lctx , "Failed to generate file of all numbers "
          + ", found zero clientId" );
      return result;
    }

    String sqlSelect = "SELECT " + sqlDecryptPhoneNumber( "csd" , true ) + " ";
    sqlSelect += ", MIN( csd.date_inserted ) AS 'date_inserted' ";

    String sqlFrom = "FROM client_subscriber_dnc csd ";

    String sqlWhere = "WHERE ( csd.client_id = " + clientId + " ) ";
    sqlWhere += "AND ( csd.active = 1 ) ";

    String sqlGroup = "GROUP BY csd.encrypt_phone ";

    String sqlOrder = "ORDER BY " + sqlDecryptPhoneNumber( "csd" , false )
        + " ASC ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlGroup + sqlOrder;

    if ( subscriberApp.isDebug() ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    if ( ( filePathWithoutExtension == null )
        || ( filePathWithoutExtension.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate file of numbers "
          + ", found zero filePathWithoutExtension" );
      return result;
    }

    result = generateListOfNumbers( filePathWithoutExtension , sql );

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean generateListOfNumbers( String filePathWithoutExtension ,
      String sql ) {
    boolean result = false;

    // will calculate total records read
    int totalRecords = 0;

    try {

      // put txt extension
      String txtFileName = filePathWithoutExtension + ".csv";
      DLog.debug( lctx , "Create text file = " + txtFileName );

      // prepare text file object
      File file = new File( txtFileName );
      FileOutputStream fos = new FileOutputStream( file );
      DataOutputStream dos = new DataOutputStream( fos );

      // create header
      dos.writeBytes( ClientSubscriberFileHeader.PHONENUMBER + ","
          + ClientSubscriberFileHeader.DATECREATED + "\n" );

      // prepare database connection
      String databaseName = "profiledb";
      ConnectionWrapper conn = dbLib.getReaderConnection( databaseName );
      if ( ( conn != null ) && ( conn.isConnected() ) ) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
          statement = conn.createStatement();
          if ( statement != null ) {
            resultSet = statement.executeQuery( sql );
          }
          if ( resultSet != null ) {
            String phoneNumber = null;
            Date dateCreated = null;
            while ( resultSet.next() ) {
              // fetch records
              phoneNumber = resultSet.getString( "phone" );
              dateCreated = DateTimeFormat.convertToDate( resultSet
                  .getString( "date_inserted" ) );
              // validate records
              if ( ( phoneNumber == null ) || ( phoneNumber.equals( "" ) ) ) {
                continue;
              }
              // clean records
              if ( phoneNumber.startsWith( "+" ) ) { // exclude "+" in phone
                phoneNumber = phoneNumber.substring( 1 );
              }
              // prepare the csv format
              StringBuffer csvLines = new StringBuffer();
              csvLines.append( StringEscapeUtils.escapeCsv( phoneNumber ) );
              csvLines.append( "," );
              csvLines.append( StringEscapeUtils
                  .escapeCsv( convertToString( dateCreated ) ) );
              csvLines.append( "\n" );
              // write records into file
              dos.writeBytes( csvLines.toString() );
              // calculate totalRecords reads
              totalRecords = totalRecords + 1;
            } // while ( resultSet.next() )
          } // if ( resultSet != null )
        } catch ( CommunicationsException communicationsException ) {
          DLog.warning( lctx , "[" + databaseName
              + "] Database query failed , " + communicationsException );
        } catch ( SQLException sqlException ) {
          DLog.warning( lctx , "[" + databaseName
              + "] Database query failed , " + sqlException );
        }
        try {
          if ( resultSet != null ) {
            resultSet.close();
            resultSet = null;
          }
        } catch ( SQLException e ) {
          DLog.error( lctx , "[" + databaseName
              + "] Failed to close the ResultSet object , " + e );
        }
        try {
          if ( statement != null ) {
            statement.close();
            statement = null;
          }
        } catch ( SQLException e ) {
          DLog.warning( lctx , "[" + databaseName
              + "] Failed to close the Statement object , " + e );
        }
        conn.disconnect( true );
      }
      DLog.debug( lctx , "Saved text file = " + txtFileName );
      dos.close();

      // generate to zip file

      String zipFilename = "";
      zipFilename += FilenameUtils.removeExtension( file.getAbsolutePath() );
      zipFilename += ".zip";

      DLog.debug( lctx , "Convert text file to zip file = " + zipFilename );

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
      DLog.debug( lctx , "Saved zip file = " + zipFilename );
      zos.close();

      // delete text file
      DLog.debug( lctx , "Delete text file = " + txtFileName );
      file.delete();

      // log summary
      DLog.debug( lctx , "Successfully created list of dnc number total = "
          + totalRecords + " record(s) saved into file name = " + zipFilename );

      // final result as true
      result = true;

    } catch ( Exception e ) {
      DLog.warning( lctx , "Failed to generate file , " + e );
    }

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String convertToString( Date date ) {
    String data = null;
    if ( date == null ) {
      return data;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat( CanonicalDateTimeFormat );
      data = sdf.format( date );
    } catch ( Exception e ) {
    }
    return data;
  }

  private String sqlDecryptPhoneNumber( String tableAlias , boolean addAsPhone ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" );
    if ( ( tableAlias != null ) && ( !tableAlias.equals( "" ) ) ) {
      sb.append( tableAlias );
      sb.append( "." );
    }
    sb.append( "encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') " );
    if ( addAsPhone ) {
      sb.append( "AS phone " );
    }
    return sb.toString();
  }

}
