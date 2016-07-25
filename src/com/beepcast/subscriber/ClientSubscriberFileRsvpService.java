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
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
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

public class ClientSubscriberFileRsvpService {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberFileRsvpService" );

  static final String SQL_QUERY_FIELDS = " , rsvp , date_rsvp ";

  static final int BUFFER = 2048;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberApp subscriberApp;
  private DatabaseLibrary dbLib;
  private final String keyPhoneNumber;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberFileRsvpService( SubscriberApp subscriberApp ) {
    this.subscriberApp = subscriberApp;
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean generateListOfAllNumbers( String filePathWithoutExtension ,
      int groupSubscriberId , ArrayList listAddFields ) {
    boolean result = false;

    if ( groupSubscriberId < 1 ) {
      DLog.warning( lctx , "Failed to generate file of numbers "
          + ", found zero groupSubscriberId" );
      return result;
    }

    String sqlSelect = "SELECT " + sqlDecryptPhoneNumber() + SQL_QUERY_FIELDS;
    String sqlFrom = "FROM client_subscriber ";
    String sqlWhere = "WHERE ( subscriber_group_id = " + groupSubscriberId
        + " ) ";

    String sql = sqlSelect + sqlFrom + sqlWhere;

    if ( StringUtils.isBlank( filePathWithoutExtension ) ) {
      DLog.warning( lctx , "Failed to generate file of numbers "
          + ", found empty filePathWithoutExtension" );
      return result;
    }

    DLog.debug( lctx , "Generating list of all numbers" );

    result = extractNumbersToFile( subscriberApp.isDebug() ,
        filePathWithoutExtension , sql , true );

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean extractNumbersToFile( boolean debug ,
      String filePathWithoutExtension , String sql , boolean subscriptionInfo ) {
    boolean result = false;

    // will calculate total records read
    int totalRecords = 0;

    try {

      // put txt extension
      String txtFileName = filePathWithoutExtension + ".csv";
      DLog.debug( lctx , "Preparing to create text file = " + txtFileName );

      // prepare text file object
      File file = new File( txtFileName );
      FileOutputStream fos = new FileOutputStream( file );
      DataOutputStream dos = new DataOutputStream( fos );

      // create header
      extractHeaderToFile( dos );

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
              DLog.debug( lctx , "Perform " + sql );
            }
            resultSet = statement.executeQuery( sql );
          }
          if ( resultSet != null ) {
            while ( resultSet.next() ) {
              if ( extractNumberToFile( dos , resultSet ) ) {
                totalRecords = totalRecords + 1;
              }
            }
          }
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
          DLog.warning( lctx , "[" + databaseName
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

      DLog.debug( lctx , "Converting text file to zip file = " + zipFilename );

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

      // log the summary
      DLog.debug( lctx , "Successfully created list of "
          + "subscriber number total = " + totalRecords
          + " record(s) saved into file name = " + zipFilename );

      // set result as true
      result = true;

    } catch ( Exception e ) {
      DLog.warning( lctx , "Failed to generate report file , " + e );
    }

    return result;
  }

  private boolean extractHeaderToFile( DataOutputStream dos )
      throws IOException {
    boolean result = false;
    if ( dos == null ) {
      return result;
    }
    dos.writeBytes( "PhoneNumber,RsvpStatus,RsvpDate\n" );
    result = true;
    return result;
  }

  private boolean extractNumberToFile( DataOutputStream dos ,
      ResultSet resultSet ) throws SQLException , IOException {
    boolean result = false;
    if ( dos == null ) {
      return result;
    }
    if ( resultSet == null ) {
      return result;
    }

    // fetch records

    String phone = resultSet.getString( "phone" );
    String rsvp = resultSet.getString( "rsvp" );
    Date dateRsvp = DateTimeFormat.convertToDate( resultSet
        .getString( "date_rsvp" ) );
    String dateRsvpStr = DateTimeFormat.convertToString( dateRsvp );

    // validate must be records
    if ( StringUtils.isBlank( phone ) ) {
      DLog.warning( lctx , "Failed to extract number to file "
          + ", found empty phone number" );
      return result;
    }

    // clean records
    if ( phone.startsWith( "+" ) ) {
      phone = phone.substring( 1 );
    }

    rsvp = StringUtils.trimToEmpty( rsvp );
    dateRsvpStr = StringUtils.trimToEmpty( dateRsvpStr );

    // compose the csv format
    StringBuffer cvsLines = new StringBuffer();
    cvsLines.append( StringEscapeUtils.escapeCsv( phone ) );
    cvsLines.append( "," );
    cvsLines.append( StringEscapeUtils.escapeCsv( rsvp ) );
    cvsLines.append( "," );
    cvsLines.append( StringEscapeUtils.escapeCsv( dateRsvpStr ) );
    cvsLines.append( "\n" );

    // write records into file
    dos.writeBytes( cvsLines.toString() );

    result = true;
    return result;
  }

  private String sqlDecryptPhoneNumber() {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') AS phone" );
    return sb.toString();
  }

}
