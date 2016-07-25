package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "SubscriberGroupDAO" );

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

  public SubscriberGroupDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public SubscriberGroupBean getSubscriberGroupBean( int id , boolean debug ) {
    SubscriberGroupBean bean = null;

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM subscriber_group sg ";
    String sqlWhere = "WHERE ( sg.id = " + id + " ) ";
    String sql = sqlSelect + sqlFrom + sqlWhere;

    // DLog.debug( lctx , "Perform " + sql );
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public SubscriberGroupBean getSubscriberGroupBean( int clientId ,
      String groupName , boolean debug ) {
    SubscriberGroupBean bean = null;

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "INNER JOIN client c ON ( c.client_id = sg.client_id ) ";
    String sqlWhere = "WHERE ( sg.active = 1 ) AND ( c.active = 1 ) ";
    if ( clientId > 0 ) {
      sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";
    }
    if ( groupName != null ) {
      sqlWhere += "AND ( sg.group_name = '"
          + StringEscapeUtils.escapeSql( groupName ) + "' ) ";
    }
    String sqlOrder = "ORDER BY sg.id DESC ";
    String sqlLimit = "LIMIT 1 ";
    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    // DLog.debug( lctx , "Perform " + sql );
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      bean = populateRecord( qr );
    }

    return bean;
  }

  public List listSubscriberGroupBeans( int clientId , int lastDays ,
      boolean orderIdAsc , int top , int limit , boolean debug ) {
    List list = new ArrayList();

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "INNER JOIN client c ON ( c.client_id = sg.client_id ) ";
    String sqlWhere = "WHERE ( sg.active = 1 ) ANd ( c.active = 1 ) ";
    if ( clientId > 0 ) {
      sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";
    }
    if ( lastDays > 0 ) {
      sqlWhere += "AND ( sg.date_inserted > DATE_SUB( NOW() , INTERVAL "
          + lastDays + " DAY ) ) ";
    }
    String sqlOrder = "ORDER BY sg.id " + ( orderIdAsc ? "ASC" : "DESC" ) + " ";
    String sqlLimit = "LIMIT " + top + " , " + limit + " ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      populateRecords( qr , list );
    }

    return list;
  }

  public List listSubscriberGroupBeans( int clientId , boolean debug ) {
    List list = new ArrayList();

    String sqlSelect = "SELECT " + populateFields();
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "INNER JOIN client c ON ( c.client_id = sg.client_id ) ";
    String sqlWhere = "WHERE ( sg.active = 1 ) ANd ( c.active = 1 ) ";
    if ( clientId > 0 ) {
      sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";
    }
    String sqlOrder = "ORDER BY sg.id ASC ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      populateRecords( qr , list );
    }

    return list;
  }

  public SubscriberGroupBean insertSubscriberGroup( int clientId ,
      String groupName , boolean debug ) {
    SubscriberGroupBean bean = null;

    String sqlInsert = "INSERT INTO subscriber_group ";
    sqlInsert += "( client_id , group_name , active , date_inserted , date_updated ) ";
    String sqlValues = "VALUES ( " + clientId + " , '"
        + StringEscapeUtils.escapeSql( groupName ) + "' , 1 , NOW() , NOW() ) ";
    String sql = sqlInsert + sqlValues;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( ( iresult != null ) && ( iresult.intValue() > 0 ) ) {
      bean = getSubscriberGroupBean( clientId , groupName , debug );
    }

    return bean;
  }

  public boolean updateSubscriberGroup( int id , String groupName ,
      boolean debug ) {
    boolean result = false;

    String sqlUpdate = "UPDATE subscriber_group ";
    String sqlSet = "SET group_name = '"
        + StringEscapeUtils.escapeSql( groupName )
        + "' , date_updated = NOW() ";
    String sqlWhere = "WHERE ( id = " + id + " ) ";
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

  public boolean setInActiveSubscriberGroup( int id , boolean debug ) {
    boolean result = false;

    String sqlUpdate = "UPDATE subscriber_group ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE ( id = " + id + " ) ";
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

  public int deleteNumbersInSubscriberGroup( int id , boolean debug ) {
    int result = 0;

    String sqlDelete = "DELETE FROM client_subscriber ";
    String sqlWhere = "WHERE ( subscriber_group_id = " + id + " ) ";
    String sql = sqlDelete + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer iresult = dbLib.executeQuery( "profiledb" , sql );
    if ( iresult != null ) {
      result = iresult.intValue();
    }

    return result;
  }

  public long getTotalSubscriberGroup( int clientId , int lastDays ,
      boolean debug ) {
    long totalRecords = 0;

    String sqlSelect = "SELECT COUNT(sg.id) AS total ";
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "INNER JOIN client c ON ( c.client_id = sg.client_id ) ";
    String sqlWhere = "WHERE ( sg.active = 1 ) AND ( c.active = 1 ) ";
    if ( clientId > 0 ) {
      sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";
    }
    if ( lastDays > 0 ) {
      sqlWhere += "AND ( sg.date_inserted > DATE_SUB( NOW() , INTERVAL "
          + lastDays + " DAY ) ) ";
    }
    String sql = sqlSelect + sqlFrom + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalRecords;
    }
    QueryItem qi = null;
    Iterator iter = qr.iterator();
    while ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }
    if ( qi == null ) {
      return totalRecords;
    }
    String stemp = qi.getFirstValue();
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        totalRecords = Long.parseLong( stemp );
      } catch ( NumberFormatException e ) {
      }
    }

    return totalRecords;
  }

  public long getTotalSubscriberGroupNumber( int clientId , boolean debug ) {
    long totalRecords = 0;

    String sqlSelect = "SELECT SUM(sgr.total_subscribed) AS total ";
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "INNER JOIN subscriber_group_report sgr ON ( sgr.subscriber_group_id = sg.id ) ";
    sqlFrom += "INNER JOIN client c ON ( c.client_id = sg.client_id ) ";
    String sqlWhere = "WHERE ( sg.active = 1 ) AND ( c.active = 1 ) ";
    if ( clientId > 0 ) {
      sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";
    }
    String sql = sqlSelect + sqlFrom + sqlWhere;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return totalRecords;
    }
    QueryItem qi = null;
    Iterator iter = qr.iterator();
    while ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }
    if ( qi == null ) {
      return totalRecords;
    }
    String stemp = qi.getFirstValue();
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        totalRecords = Long.parseLong( stemp );
      } catch ( NumberFormatException e ) {
      }
    }

    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String populateFields() {
    String fields = "sg.id , sg.client_id , sg.group_name , sg.active ";
    return fields;
  }

  private int populateRecords( QueryResult qr , List list ) {
    int totalRecords = 0;

    QueryItem qi;
    SubscriberGroupBean bean;
    Iterator iter = qr.iterator();
    while ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
      if ( qi == null ) {
        continue;
      }
      bean = populateRecord( qi );
      if ( bean == null ) {
        continue;
      }
      list.add( bean );
    }

    totalRecords = list.size();
    return totalRecords;
  }

  private SubscriberGroupBean populateRecord( QueryResult qr ) {
    SubscriberGroupBean bean = null;

    QueryItem qi = null;
    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }
    if ( qi != null ) {
      bean = populateRecord( qi );
    }

    return bean;
  }

  private SubscriberGroupBean populateRecord( QueryItem qi ) {
    SubscriberGroupBean bean = null;

    String stemp;
    int itemp;
    boolean btemp;

    boolean valid = true;
    SubscriberGroupBean tempBean = new SubscriberGroupBean();

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        if ( itemp < 1 ) {
          DLog.warning( lctx , "Failed to get "
              + "group subscriber , found zero id" );
          valid = false;
        } else {
          tempBean.setId( itemp );
        }
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // client_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        if ( itemp < 1 ) {
          DLog.warning( lctx , "Failed to get "
              + "group subscriber , found zero clientId" );
          valid = false;
        } else {
          tempBean.setClientId( itemp );
        }
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // group_name
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      tempBean.setGroupName( stemp );
    }

    stemp = (String) qi.get( 3 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        btemp = itemp > 0;
        tempBean.setActive( btemp );
      } catch ( NumberFormatException e ) {
      }
    }

    if ( valid ) {
      bean = tempBean;
    }

    return bean;
  }

}
