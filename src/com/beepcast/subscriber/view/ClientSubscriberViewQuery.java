package com.beepcast.subscriber.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.util.DateTimeFormat;

public class ClientSubscriberViewQuery {

  public static String sqlSelectFrom( String keyPhoneNumber ) {

    String sqlSelect = "SELECT cs.id AS 'csid' , sg.id AS 'sgid' ";
    sqlSelect += ", sg.group_name , "
        + sqlDecryptPhoneNumber( "cs" , keyPhoneNumber , "phone" ) + " ";
    sqlSelect += ", cs.subscribed , cs.date_subscribed ";
    sqlSelect += ", cs.global_subscribed , cs.date_global_subscribed ";
    sqlSelect += ", cs.global_invalid , cs.date_global_invalid ";
    sqlSelect += ", cs.global_dnc , cs.date_global_dnc ";

    String sqlFrom = "FROM client_subscriber cs ";
    sqlFrom += "INNER JOIN subscriber_group sg ON ( sg.id = cs.subscriber_group_id ) ";

    String sqlSelectFrom = sqlSelect + sqlFrom;

    return sqlSelectFrom;

  }

  public static List populateBeans( QueryResult qr ) {
    List listBeans = new ArrayList();

    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return listBeans;
    }

    ClientSubscriberViewBean bean = null;
    Iterator it = qr.iterator();
    while ( it.hasNext() ) {
      bean = populateBean( (QueryItem) it.next() );
      if ( bean == null ) {
        continue;
      }
      listBeans.add( bean );
    }

    return listBeans;
  }

  public static ClientSubscriberViewBean populateBean( QueryItem qi ) {
    ClientSubscriberViewBean bean = null;

    if ( qi == null ) {
      return bean;
    }

    String stemp = null;

    bean = new ClientSubscriberViewBean();

    stemp = (String) qi.get( 0 ); // csid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setClientSubscriberId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // sgid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setSubscriberGroupId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // sg.group_name
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setSubscriberGroupName( stemp );
    }

    stemp = (String) qi.get( 3 ); // cs.phone
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setPhoneNumber( stemp );
    }

    stemp = (String) qi.get( 4 ); // cs.subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setSubscribed( stemp.equals( "1" ) );
    }

    stemp = (String) qi.get( 5 ); // cs.date_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateSubscribed( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 6 ); // cs.global_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setGlobalSubscribed( stemp.equals( "1" ) );
    }

    stemp = (String) qi.get( 7 ); // cs.date_global_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalSubscribed( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 8 ); // cs.global_invalid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setGlobalInvalid( stemp.equals( "1" ) );
    }

    stemp = (String) qi.get( 9 ); // cs.date_global_invalid
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalInvalid( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 10 ); // cs.global_dnc
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setGlobalDnc( stemp.equals( "1" ) );
    }

    stemp = (String) qi.get( 11 ); // cs.date_global_dnc
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateGlobalDnc( DateTimeFormat.convertToDate( stemp ) );
    }

    return bean;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public static String sqlDecryptPhoneNumber( String tableAlias ,
      String keyPhoneNumber , String aliasName ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" );
    sb.append( tableAlias );
    sb.append( ".encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') " );
    if ( ( aliasName != null ) && ( !aliasName.equals( "" ) ) ) {
      sb.append( "AS " );
      sb.append( aliasName );
      sb.append( " " );
    }
    return sb.toString();
  }

}
