package com.beepcast.subscriber.view;

import java.util.Iterator;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class GroupSubscriberViewDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "GroupSubscriberInfoDAO" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private DatabaseLibrary dbLib;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public GroupSubscriberViewDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public GroupSubscriberViewBean getBean( int groupSubscriberId ) {
    GroupSubscriberViewBean bean = null;

    String sqlSelect = "SELECT sg.id , sg.group_name ";
    sqlSelect += ", sgr.total_uploaded AS total_subscriber ";
    sqlSelect += ", sgr.total_subscribed AS total_subscribed ";
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "LEFT OUTER JOIN subscriber_group_report sgr ";
    sqlFrom += "ON sg.id = sgr.subscriber_group_id ";
    String sqlWhere = "WHERE ( sg.active = 1 ) ";
    sqlWhere += "AND ( sg.id = " + groupSubscriberId + " ) ";

    String sql = sqlSelect + sqlFrom + sqlWhere;

    DLog.debug( lctx , "Perform " + sql );

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( qr != null && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private GroupSubscriberViewBean populateRecord( QueryResult qr ) {
    GroupSubscriberViewBean bean = null;

    boolean valid;
    String stemp;
    int itemp;

    QueryItem qi = null;

    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }

    if ( qi == null ) {
      return bean;
    }

    valid = true;
    GroupSubscriberViewBean tempBean = new GroupSubscriberViewBean();
    stemp = (String) qi.get( 0 );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        if ( itemp < 1 ) {
          DLog.warning( lctx , "Failed to get group subscriber bean "
              + ", found zero id" );
          valid = false;
        } else {
          tempBean.setId( itemp );
        }
      } catch ( NumberFormatException e ) {
      }
    }
    stemp = (String) qi.get( 1 );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      tempBean.setGroupName( stemp );
    }
    stemp = (String) qi.get( 2 );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        tempBean.setTotalSubcriber( itemp );
      } catch ( NumberFormatException e ) {
      }
    }
    stemp = (String) qi.get( 3 );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        tempBean.setTotalSubscribed( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    if ( valid ) {
      bean = tempBean;
    }

    return bean;
  }

}
