package com.beepcast.subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberGroupReportParamDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static final DLogContext lctx = new SimpleContext(
      "SubscriberGroupReportParamDAO" );

  private static final String SQL_QUERY_SELECT = "SELECT id , subscriber_group_id "
      + ", field , value , description , active ";

  private static final String SQL_QUERY_FROM = "FROM subscriber_group_report_param ";

  private static final int MAX_LIMIT_RECORDS = 1000;

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

  public SubscriberGroupReportParamDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean insertBean( SubscriberGroupReportParamBean bean ) {
    boolean result = false;

    if ( bean == null ) {
      return result;
    }

    String field = StringUtils.trimToEmpty( bean.getField() );
    String value = StringUtils.trimToEmpty( bean.getValue() );
    String description = StringUtils.trimToEmpty( bean.getDescription() );
    int active = bean.isActive() ? 1 : 0;

    String sqlInsert = "INSERT subscriber_group_report_param ";
    sqlInsert += "(subscriber_group_id,field,value,description";
    sqlInsert += ",active,date_inserted,date_updated) ";

    String sqlValue = "VALUES (" + bean.getSubscriberGroupId() + ",'"
        + StringEscapeUtils.escapeSql( field ) + "','"
        + StringEscapeUtils.escapeSql( value ) + "','"
        + StringEscapeUtils.escapeSql( description ) + "'," + active
        + ",NOW(),NOW()) ";

    String sql = sqlInsert + sqlValue;

    DLog.debug( lctx , "Perform " + sql );
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public SubscriberGroupReportParamBean queryBean( int id ) {
    SubscriberGroupReportParamBean bean = null;

    if ( id < 1 ) {
      return bean;
    }

    String sqlSelect = SQL_QUERY_SELECT;
    String sqlFrom = SQL_QUERY_FROM;
    String sqlWhere = "WHERE ( id = " + id + " ) ";

    String sql = sqlSelect + sqlFrom + sqlWhere;

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      QueryItem qi = null;
      Iterator iter = qr.iterator();
      if ( iter.hasNext() ) {
        qi = (QueryItem) iter.next();
      }
      if ( qi != null ) {
        bean = populateBean( qi );
      }
    }

    return bean;
  }

  public SubscriberGroupReportParamBean queryBean( int subscriberGroupId ,
      String field ) {
    SubscriberGroupReportParamBean bean = null;

    if ( subscriberGroupId < 1 ) {
      return bean;
    }
    if ( StringUtils.isBlank( field ) ) {
      return bean;
    }

    String sqlSelect = SQL_QUERY_SELECT;
    String sqlFrom = SQL_QUERY_FROM;
    String sqlWhere = "WHERE ( active = 1 ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( field = '" + StringEscapeUtils.escapeSql( field )
        + "' ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      QueryItem qi = null;
      Iterator iter = qr.iterator();
      if ( iter.hasNext() ) {
        qi = (QueryItem) iter.next();
      }
      if ( qi != null ) {
        bean = populateBean( qi );
      }
    }

    return bean;
  }

  public List queryBeans( int subscriberGroupId , String prefixField ,
      boolean debug ) {
    List list = new ArrayList();

    if ( subscriberGroupId < 1 ) {
      return list;
    }
    if ( StringUtils.isBlank( prefixField ) ) {
      return list;
    }

    String sqlSelect = SQL_QUERY_SELECT;
    String sqlFrom = SQL_QUERY_FROM;
    String sqlWhere = "WHERE ( active = 1 ) ";
    sqlWhere += "AND ( subscriber_group_id = " + subscriberGroupId + " ) ";
    sqlWhere += "AND ( field LIKE '"
        + StringEscapeUtils.escapeSql( prefixField ) + "%' ) ";
    String sqlOrder = "ORDER BY id ASC ";
    String sqlLimit = "LIMIT " + MAX_LIMIT_RECORDS + " ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      Iterator iter = qr.iterator();
      while ( iter.hasNext() ) {
        QueryItem qi = (QueryItem) iter.next();
        if ( qi == null ) {
          continue;
        }
        SubscriberGroupReportParamBean bean = populateBean( qi );
        if ( bean == null ) {
          continue;
        }
        list.add( bean );
      }
    }

    return list;
  }

  public boolean updateActiveBeanById( int id , boolean active ) {
    boolean result = false;

    if ( id < 1 ) {
      return result;
    }

    String sqlUpdate = "UPDATE subscriber_group_report_param ";
    String sqlSet = "SET active = " + ( active ? 1 : 0 )
        + " , date_updated = NOW() ";
    String sqlWhere = "WHERE ( id = " + id + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    DLog.debug( lctx , "Perform " + sql );
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public boolean updateActiveBeanBySubscriberGroupId( int subscriberGroupId ,
      boolean active ) {
    boolean result = false;

    if ( subscriberGroupId < 1 ) {
      return result;
    }

    String sqlUpdate = "UPDATE subscriber_group_report_param ";
    String sqlSet = "SET active = " + ( active ? 1 : 0 )
        + " , date_updated = NOW() ";
    String sqlWhere = "WHERE ( subscriber_group_id = " + subscriberGroupId
        + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    DLog.debug( lctx , "Perform " + sql );
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  public int deleteAllInactiveBeans() {
    int totalRecords = 0;

    String sqlDelete = "DELETE FROM subscriber_group_report_param ";
    String sqlWhere = "WHERE ( active = 0 ) ";

    String sql = sqlDelete + sqlWhere;

    DLog.debug( lctx , "Perform " + sql );
    Integer irslt = dbLib.executeQuery( "profiledb" , sql );
    if ( irslt != null ) {
      totalRecords = irslt.intValue();
    }

    return totalRecords;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private SubscriberGroupReportParamBean populateBean( QueryItem qi ) {
    SubscriberGroupReportParamBean bean = SubscriberGroupReportParamFactory
        .createSubscriberGroupReportParamBean();

    if ( qi == null ) {
      return bean;
    }

    String stemp;
    int itemp;

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setId( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // subscriber_group_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        itemp = Integer.parseInt( stemp );
        bean.setSubscriberGroupId( itemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 2 ); // field
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setField( stemp );
    }

    stemp = (String) qi.get( 3 ); // value
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setValue( stemp );
    }

    stemp = (String) qi.get( 4 ); // description
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDescription( stemp );
    }

    stemp = (String) qi.get( 5 ); // active
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setActive( stemp.equals( "1" ) );
    }

    return bean;
  }

}
