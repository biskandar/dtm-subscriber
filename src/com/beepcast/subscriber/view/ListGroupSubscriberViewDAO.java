package com.beepcast.subscriber.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.dbmanager.util.DateTimeFormat;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ListGroupSubscriberViewDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext(
      "ListGroupSubscriberViewDAO" );

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

  public ListGroupSubscriberViewDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public long totalBeans( int clientId , String inKeywordGroupNames ,
      String exKeywordGroupNames , boolean debug ) {
    long totalRecords = 0;
    if ( clientId < 1 ) {
      return totalRecords;
    }

    // compose sql
    String sqlSelect = "SELECT sg.id ";
    String sqlFrom = sqlFrom();
    String sqlWhere = sqlWhere( clientId , inKeywordGroupNames ,
        exKeywordGroupNames );
    String sql = sqlSelect + sqlFrom + sqlWhere;

    sql = "SELECT COUNT(*) AS total FROM ( " + sql + " ) tmp ";

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    // execute and fetch query
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      Iterator iqr = qr.iterator();
      QueryItem qi = null;
      if ( iqr.hasNext() ) {
        qi = (QueryItem) iqr.next();
      }
      if ( qi != null ) {
        try {
          totalRecords = Long.parseLong( qi.getFirstValue() );
        } catch ( NumberFormatException e ) {
        }
      }
    }

    return totalRecords;
  }

  public List selectBeans( int clientId , String inKeywordGroupNames ,
      String exKeywordGroupNames , int top , int limit , int orderBy ,
      boolean debug ) {
    List listGroupSubscriberInfoBeans = new ArrayList();
    if ( clientId < 1 ) {
      return listGroupSubscriberInfoBeans;
    }

    // compose sql

    String sqlSelect = "SELECT sg.id , sg.group_name ";
    sqlSelect += ", sgr.total_uploaded AS total_subscriber ";
    sqlSelect += ", sgr.total_subscribed AS total_subscribed ";
    sqlSelect += ", sg.date_inserted , sg.date_updated ";

    String sqlFrom = sqlFrom();

    String sqlWhere = sqlWhere( clientId , inKeywordGroupNames ,
        exKeywordGroupNames );

    String sqlOrderBy = "";
    if ( orderBy == ListGroupSubscriberViewService.ORDERBY_IDASC ) {
      sqlOrderBy = "ORDER BY sg.id ASC ";
    }
    if ( orderBy == ListGroupSubscriberViewService.ORDERBY_IDDESC ) {
      sqlOrderBy = "ORDER BY sg.id DESC ";
    }
    if ( orderBy == ListGroupSubscriberViewService.ORDERBY_NAMEASC ) {
      sqlOrderBy = "ORDER BY sg.group_name ASC ";
    }
    if ( orderBy == ListGroupSubscriberViewService.ORDERBY_NAMEDESC ) {
      sqlOrderBy = "ORDER BY sg.group_name DESC ";
    }

    String sqlLimit = "";
    if ( limit > 0 ) {
      sqlLimit = "LIMIT " + top + " , " + limit + " ";
    }

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrderBy + sqlLimit;

    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }

    // execute and fetch query
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      Iterator iqr = qr.iterator();
      QueryItem qi = null;
      while ( iqr.hasNext() ) {
        qi = (QueryItem) iqr.next();
        if ( qi == null ) {
          continue;
        }
        GroupSubscriberViewBean bean = populateBean( qi );
        if ( bean == null ) {
          continue;
        }
        listGroupSubscriberInfoBeans.add( bean );
      }
    }

    return listGroupSubscriberInfoBeans;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlFrom() {
    String sqlFrom = "FROM subscriber_group sg ";
    sqlFrom += "LEFT OUTER JOIN subscriber_group_report sgr ON sg.id = sgr.subscriber_group_id ";
    return sqlFrom;
  }

  private String sqlWhere( int clientId , String inKeywordGroupNames ,
      String exKeywordGroupNames ) {
    String sqlWhere = "WHERE ( sg.active = 1 ) ";

    sqlWhere += "AND ( sg.client_id = " + clientId + " ) ";

    if ( !StringUtils.isBlank( inKeywordGroupNames ) ) {
      String sqlWhereKeywords = ListGroupSubscriberViewQuery
          .sqlWhereLikeKeywords( inKeywordGroupNames , "," , "OR" ,
              "sg.group_name" , true );
      if ( ( sqlWhereKeywords != null ) && ( !sqlWhereKeywords.equals( "" ) ) ) {
        sqlWhere += "AND ( " + sqlWhereKeywords + " ) ";
      }
    }

    if ( !StringUtils.isBlank( exKeywordGroupNames ) ) {
      String sqlWhereKeywords = ListGroupSubscriberViewQuery
          .sqlWhereLikeKeywords( exKeywordGroupNames , "," , "AND" ,
              "sg.group_name" , false );
      if ( ( sqlWhereKeywords != null ) && ( !sqlWhereKeywords.equals( "" ) ) ) {
        sqlWhere += "AND ( " + sqlWhereKeywords + " ) ";
      }
    }

    return sqlWhere;
  }

  private GroupSubscriberViewBean populateBean( QueryItem qi ) {
    GroupSubscriberViewBean bean = null;

    if ( qi == null ) {
      return bean;
    }

    String stemp;

    bean = new GroupSubscriberViewBean();

    stemp = (String) qi.get( 0 ); // sg.id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setId( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // sg.group_name
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setGroupName( stemp );
    }

    stemp = (String) qi.get( 2 ); // total_subscriber
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setTotalSubcriber( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 3 ); // total_subscribed
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        bean.setTotalSubscribed( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 4 ); // sg.date_inserted
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateInserted( DateTimeFormat.convertToDate( stemp ) );
    }

    stemp = (String) qi.get( 5 ); // sg.date_updated
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      bean.setDateUpdated( DateTimeFormat.convertToDate( stemp ) );
    }

    return bean;
  }

}
