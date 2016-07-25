package com.beepcast.subscriber;

import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientSubscriberCustomDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ClientSubscriberCustomDAO" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private DatabaseLibrary dbLib;
  private final String keyCustom;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ClientSubscriberCustomDAO() {
    dbLib = DatabaseLibrary.getInstance();

    EncryptApp encryptApp = EncryptApp.getInstance();
    keyCustom = encryptApp.getKeyValue( EncryptApp.KEYNAME_CUSTOM );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insert( boolean debug , ClientSubscriberCustomBean bean ) {
    boolean result = false;

    if ( bean == null ) {
      return result;
    }

    // clean the bean
    cleanClientSubscriberCustomBean( bean );

    // compose sql
    String sqlInsert = "INSERT INTO client_subscriber_custom ( client_subscriber_id ";
    sqlInsert += ",  custom_0 ,  encrypt_custom_0 ,  custom_1 ,  encrypt_custom_1 ";
    sqlInsert += ",  custom_2 ,  encrypt_custom_2 ,  custom_3 ,  encrypt_custom_3 ";
    sqlInsert += ",  custom_4 ,  encrypt_custom_4 ,  custom_5 ,  encrypt_custom_5 ";
    sqlInsert += ",  custom_6 ,  encrypt_custom_6 ,  custom_7 ,  encrypt_custom_7 ";
    sqlInsert += ",  custom_8 ,  encrypt_custom_8 ,  custom_9 ,  encrypt_custom_9 ";
    sqlInsert += ", custom_10 , encrypt_custom_10 , custom_11 , encrypt_custom_11 ";
    sqlInsert += ", custom_12 , encrypt_custom_12 , custom_13 , encrypt_custom_13 ";
    sqlInsert += ", custom_14 , encrypt_custom_14 , custom_15 , encrypt_custom_15 ";
    sqlInsert += ", custom_16 , encrypt_custom_16 , custom_17 , encrypt_custom_17 ";
    sqlInsert += ", custom_18 , encrypt_custom_18 , custom_19 , encrypt_custom_19 ";
    sqlInsert += ", custom_20 , encrypt_custom_20 , custom_21 , encrypt_custom_21 ";
    sqlInsert += ", custom_22 , encrypt_custom_22 , custom_23 , encrypt_custom_23 ";
    sqlInsert += ", custom_24 , encrypt_custom_24 , custom_25 , encrypt_custom_25 ";
    sqlInsert += ", custom_26 , encrypt_custom_26 , custom_27 , encrypt_custom_27 ";
    sqlInsert += ", custom_28 , encrypt_custom_28 , custom_29 , encrypt_custom_29 ";
    sqlInsert += ", custom_30 , encrypt_custom_30 , date_inserted , date_updated ) ";
    String sqlValues = "VALUES (" + bean.getClientSubscriberId() + ",'',"
        + sqlEncryptCustom( bean.getCustom0() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom1() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom2() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom3() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom4() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom5() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom6() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom7() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom8() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom9() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom10() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom11() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom12() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom13() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom14() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom15() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom16() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom17() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom18() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom19() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom20() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom21() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom22() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom23() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom24() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom25() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom26() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom27() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom28() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom29() ) + ",'',"
        + sqlEncryptCustom( bean.getCustom30() ) + ",NOW(),NOW()) ";
    String sql = sqlInsert + sqlValues;

    // execute sql
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public ClientSubscriberCustomBean select( boolean debug ,
      int clientSubscriberId ) {
    ClientSubscriberCustomBean bean = null;

    // compose sql
    String sqlSelectFrom = sqlSelectFrom();
    String sqlWhere = "WHERE ( client_subscriber_id = " + clientSubscriberId
        + " ) ";
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

  public boolean update( boolean debug , ClientSubscriberCustomBean bean ) {
    boolean result = false;

    if ( bean == null ) {
      return result;
    }

    // prepare the sql update
    StringBuffer sbSqlSet = new StringBuffer();
    composeUpdateSqlSet( sbSqlSet , "custom_0" , bean.getCustom0() );
    composeUpdateSqlSet( sbSqlSet , "custom_1" , bean.getCustom1() );
    composeUpdateSqlSet( sbSqlSet , "custom_2" , bean.getCustom2() );
    composeUpdateSqlSet( sbSqlSet , "custom_3" , bean.getCustom3() );
    composeUpdateSqlSet( sbSqlSet , "custom_4" , bean.getCustom4() );
    composeUpdateSqlSet( sbSqlSet , "custom_5" , bean.getCustom5() );
    composeUpdateSqlSet( sbSqlSet , "custom_6" , bean.getCustom6() );
    composeUpdateSqlSet( sbSqlSet , "custom_7" , bean.getCustom7() );
    composeUpdateSqlSet( sbSqlSet , "custom_8" , bean.getCustom8() );
    composeUpdateSqlSet( sbSqlSet , "custom_9" , bean.getCustom9() );
    composeUpdateSqlSet( sbSqlSet , "custom_10" , bean.getCustom10() );
    composeUpdateSqlSet( sbSqlSet , "custom_11" , bean.getCustom11() );
    composeUpdateSqlSet( sbSqlSet , "custom_12" , bean.getCustom12() );
    composeUpdateSqlSet( sbSqlSet , "custom_13" , bean.getCustom13() );
    composeUpdateSqlSet( sbSqlSet , "custom_14" , bean.getCustom14() );
    composeUpdateSqlSet( sbSqlSet , "custom_15" , bean.getCustom15() );
    composeUpdateSqlSet( sbSqlSet , "custom_16" , bean.getCustom16() );
    composeUpdateSqlSet( sbSqlSet , "custom_17" , bean.getCustom17() );
    composeUpdateSqlSet( sbSqlSet , "custom_18" , bean.getCustom18() );
    composeUpdateSqlSet( sbSqlSet , "custom_19" , bean.getCustom19() );
    composeUpdateSqlSet( sbSqlSet , "custom_20" , bean.getCustom20() );
    composeUpdateSqlSet( sbSqlSet , "custom_21" , bean.getCustom21() );
    composeUpdateSqlSet( sbSqlSet , "custom_22" , bean.getCustom22() );
    composeUpdateSqlSet( sbSqlSet , "custom_23" , bean.getCustom23() );
    composeUpdateSqlSet( sbSqlSet , "custom_24" , bean.getCustom24() );
    composeUpdateSqlSet( sbSqlSet , "custom_25" , bean.getCustom25() );
    composeUpdateSqlSet( sbSqlSet , "custom_26" , bean.getCustom26() );
    composeUpdateSqlSet( sbSqlSet , "custom_27" , bean.getCustom27() );
    composeUpdateSqlSet( sbSqlSet , "custom_28" , bean.getCustom28() );
    composeUpdateSqlSet( sbSqlSet , "custom_29" , bean.getCustom29() );
    composeUpdateSqlSet( sbSqlSet , "custom_30" , bean.getCustom30() );
    if ( sbSqlSet.toString().equals( "" ) ) {
      DLog.warning( lctx , "Failed to update , found blank sbSqlSet" );
      return result;
    }
    sbSqlSet.append( ", date_updated = NOW() " );

    // compose sql
    String sqlUpdate = "UPDATE client_subscriber_custom ";
    String sqlWhere = "WHERE ( id = " + bean.getId() + " ) ";
    String sql = sqlUpdate + sbSqlSet.toString() + sqlWhere;

    // execute sql
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlSelectFrom() {
    String sqlSelect = "SELECT id,client_subscriber_id,"
        + sqlDecryptCustom( "custom_0" ) + "," + sqlDecryptCustom( "custom_1" )
        + "," + sqlDecryptCustom( "custom_2" ) + ","
        + sqlDecryptCustom( "custom_3" ) + "," + sqlDecryptCustom( "custom_4" )
        + "," + sqlDecryptCustom( "custom_5" ) + ","
        + sqlDecryptCustom( "custom_6" ) + "," + sqlDecryptCustom( "custom_7" )
        + "," + sqlDecryptCustom( "custom_8" ) + ","
        + sqlDecryptCustom( "custom_9" ) + "," + sqlDecryptCustom( "custom_10" )
        + "," + sqlDecryptCustom( "custom_11" ) + ","
        + sqlDecryptCustom( "custom_12" ) + ","
        + sqlDecryptCustom( "custom_13" ) + ","
        + sqlDecryptCustom( "custom_14" ) + ","
        + sqlDecryptCustom( "custom_15" ) + ","
        + sqlDecryptCustom( "custom_16" ) + ","
        + sqlDecryptCustom( "custom_17" ) + ","
        + sqlDecryptCustom( "custom_18" ) + ","
        + sqlDecryptCustom( "custom_19" ) + ","
        + sqlDecryptCustom( "custom_20" ) + ","
        + sqlDecryptCustom( "custom_21" ) + ","
        + sqlDecryptCustom( "custom_22" ) + ","
        + sqlDecryptCustom( "custom_23" ) + ","
        + sqlDecryptCustom( "custom_24" ) + ","
        + sqlDecryptCustom( "custom_25" ) + ","
        + sqlDecryptCustom( "custom_26" ) + ","
        + sqlDecryptCustom( "custom_27" ) + ","
        + sqlDecryptCustom( "custom_28" ) + ","
        + sqlDecryptCustom( "custom_29" ) + ","
        + sqlDecryptCustom( "custom_30" ) + " ";
    String sqlFrom = "FROM client_subscriber_custom ";
    String sqlSelectFrom = sqlSelect + sqlFrom;
    return sqlSelectFrom;
  }

  private ClientSubscriberCustomBean populateRecord( QueryResult qr ) {
    ClientSubscriberCustomBean bean = null;
    if ( qr == null ) {
      return bean;
    }
    Iterator it = qr.getIterator();
    if ( !it.hasNext() ) {
      return bean;
    }
    bean = populateRecord( (QueryItem) it.next() );
    return bean;
  }

  private ClientSubscriberCustomBean populateRecord( QueryItem qi ) {
    ClientSubscriberCustomBean bean = null;

    if ( qi == null ) {
      return bean;
    }

    bean = new ClientSubscriberCustomBean();

    String stemp;

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // client_subscriber_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setClientSubscriberId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // custom_0
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom0( stemp );
    }

    stemp = (String) qi.get( 3 ); // custom_1
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom1( stemp );
    }

    stemp = (String) qi.get( 4 ); // custom_2
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom2( stemp );
    }

    stemp = (String) qi.get( 5 ); // custom_3
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom3( stemp );
    }

    stemp = (String) qi.get( 6 ); // custom_4
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom4( stemp );
    }

    stemp = (String) qi.get( 7 ); // custom_5
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom5( stemp );
    }

    stemp = (String) qi.get( 8 ); // custom_6
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom6( stemp );
    }

    stemp = (String) qi.get( 9 ); // custom_7
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom7( stemp );
    }

    stemp = (String) qi.get( 10 ); // custom_8
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom8( stemp );
    }

    stemp = (String) qi.get( 11 ); // custom_9
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom9( stemp );
    }

    stemp = (String) qi.get( 12 ); // custom_10
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom10( stemp );
    }

    stemp = (String) qi.get( 13 ); // custom_11
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom11( stemp );
    }

    stemp = (String) qi.get( 14 ); // custom_12
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom12( stemp );
    }

    stemp = (String) qi.get( 15 ); // custom_13
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom13( stemp );
    }

    stemp = (String) qi.get( 16 ); // custom_14
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom14( stemp );
    }

    stemp = (String) qi.get( 17 ); // custom_15
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom15( stemp );
    }

    stemp = (String) qi.get( 18 ); // custom_16
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom16( stemp );
    }

    stemp = (String) qi.get( 19 ); // custom_17
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom17( stemp );
    }

    stemp = (String) qi.get( 20 ); // custom_18
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom18( stemp );
    }

    stemp = (String) qi.get( 21 ); // custom_19
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom19( stemp );
    }

    stemp = (String) qi.get( 22 ); // custom_20
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom20( stemp );
    }

    stemp = (String) qi.get( 23 ); // custom_21
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom21( stemp );
    }

    stemp = (String) qi.get( 24 ); // custom_22
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom22( stemp );
    }

    stemp = (String) qi.get( 25 ); // custom_23
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom23( stemp );
    }

    stemp = (String) qi.get( 26 ); // custom_24
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom24( stemp );
    }

    stemp = (String) qi.get( 27 ); // custom_25
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom25( stemp );
    }

    stemp = (String) qi.get( 28 ); // custom_26
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom26( stemp );
    }

    stemp = (String) qi.get( 29 ); // custom_27
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom27( stemp );
    }

    stemp = (String) qi.get( 30 ); // custom_28
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom28( stemp );
    }

    stemp = (String) qi.get( 31 ); // custom_29
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom29( stemp );
    }

    stemp = (String) qi.get( 32 ); // custom_30
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setCustom30( stemp );
    }

    return bean;
  }

  private void composeUpdateSqlSet( StringBuffer sbSqlSet , String customField ,
      String customValue ) {

    if ( sbSqlSet == null ) {
      return;
    }
    if ( StringUtils.isBlank( customField ) ) {
      return;
    }
    if ( customValue == null ) {
      return;
    }

    if ( sbSqlSet.toString().equals( "" ) ) {
      sbSqlSet.append( "SET " );
    } else {
      sbSqlSet.append( ", " );
    }

    sbSqlSet.append( customField );
    sbSqlSet.append( " = '' , encrypt_" );
    sbSqlSet.append( customField );
    sbSqlSet.append( " = " );
    sbSqlSet.append( sqlEncryptCustom( customValue ) );
    sbSqlSet.append( " " );

  }

  private boolean cleanClientSubscriberCustomBean(
      ClientSubscriberCustomBean bean ) {
    boolean result = false;
    if ( bean == null ) {
      return result;
    }

    // read data
    String custom0 = bean.getCustom0();
    String custom1 = bean.getCustom1();
    String custom2 = bean.getCustom2();
    String custom3 = bean.getCustom3();
    String custom4 = bean.getCustom4();
    String custom5 = bean.getCustom5();
    String custom6 = bean.getCustom6();
    String custom7 = bean.getCustom7();
    String custom8 = bean.getCustom8();
    String custom9 = bean.getCustom9();
    String custom10 = bean.getCustom10();
    String custom11 = bean.getCustom11();
    String custom12 = bean.getCustom12();
    String custom13 = bean.getCustom13();
    String custom14 = bean.getCustom14();
    String custom15 = bean.getCustom15();
    String custom16 = bean.getCustom16();
    String custom17 = bean.getCustom17();
    String custom18 = bean.getCustom18();
    String custom19 = bean.getCustom19();
    String custom20 = bean.getCustom20();
    String custom21 = bean.getCustom21();
    String custom22 = bean.getCustom22();
    String custom23 = bean.getCustom23();
    String custom24 = bean.getCustom24();
    String custom25 = bean.getCustom25();
    String custom26 = bean.getCustom26();
    String custom27 = bean.getCustom27();
    String custom28 = bean.getCustom28();
    String custom29 = bean.getCustom29();
    String custom30 = bean.getCustom30();

    // clean data
    bean.setCustom0( ( custom0 == null ) ? "" : custom0.trim() );
    bean.setCustom1( ( custom1 == null ) ? "" : custom1.trim() );
    bean.setCustom2( ( custom2 == null ) ? "" : custom2.trim() );
    bean.setCustom3( ( custom3 == null ) ? "" : custom3.trim() );
    bean.setCustom4( ( custom4 == null ) ? "" : custom4.trim() );
    bean.setCustom5( ( custom5 == null ) ? "" : custom5.trim() );
    bean.setCustom6( ( custom6 == null ) ? "" : custom6.trim() );
    bean.setCustom7( ( custom7 == null ) ? "" : custom7.trim() );
    bean.setCustom8( ( custom8 == null ) ? "" : custom8.trim() );
    bean.setCustom9( ( custom9 == null ) ? "" : custom9.trim() );
    bean.setCustom10( ( custom10 == null ) ? "" : custom10.trim() );
    bean.setCustom11( ( custom11 == null ) ? "" : custom11.trim() );
    bean.setCustom12( ( custom12 == null ) ? "" : custom12.trim() );
    bean.setCustom13( ( custom13 == null ) ? "" : custom13.trim() );
    bean.setCustom14( ( custom14 == null ) ? "" : custom14.trim() );
    bean.setCustom15( ( custom15 == null ) ? "" : custom15.trim() );
    bean.setCustom16( ( custom16 == null ) ? "" : custom16.trim() );
    bean.setCustom17( ( custom17 == null ) ? "" : custom17.trim() );
    bean.setCustom18( ( custom18 == null ) ? "" : custom18.trim() );
    bean.setCustom19( ( custom19 == null ) ? "" : custom19.trim() );
    bean.setCustom20( ( custom20 == null ) ? "" : custom20.trim() );
    bean.setCustom21( ( custom21 == null ) ? "" : custom21.trim() );
    bean.setCustom22( ( custom22 == null ) ? "" : custom22.trim() );
    bean.setCustom23( ( custom23 == null ) ? "" : custom23.trim() );
    bean.setCustom24( ( custom24 == null ) ? "" : custom24.trim() );
    bean.setCustom25( ( custom25 == null ) ? "" : custom25.trim() );
    bean.setCustom26( ( custom26 == null ) ? "" : custom26.trim() );
    bean.setCustom27( ( custom27 == null ) ? "" : custom27.trim() );
    bean.setCustom28( ( custom28 == null ) ? "" : custom28.trim() );
    bean.setCustom29( ( custom29 == null ) ? "" : custom29.trim() );
    bean.setCustom30( ( custom30 == null ) ? "" : custom30.trim() );

    result = true;
    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlEncryptCustom( String customValue ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_ENCRYPT('" );
    sb.append( StringEscapeUtils.escapeSql( customValue ) );
    sb.append( "','" );
    sb.append( keyCustom );
    sb.append( "')" );
    return sb.toString();
  }

  private String sqlDecryptCustom( String customField ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(encrypt_" + customField + ",'" );
    sb.append( keyCustom );
    sb.append( "') AS " );
    sb.append( customField );
    return sb.toString();
  }

}
