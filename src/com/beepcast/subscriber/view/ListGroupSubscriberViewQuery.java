package com.beepcast.subscriber.view;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class ListGroupSubscriberViewQuery {

  public static String sqlWhereLikeKeywords( String contentKeywords ,
      String contentSeparator , String sqlCondition , String sqlField ,
      boolean sqlLike ) {
    StringBuffer strBuf = null;
    String[] arrKeywords = contentKeywords.split( contentSeparator );
    for ( int idx = 0 ; idx < arrKeywords.length ; idx++ ) {
      String keyword = arrKeywords[idx];
      if ( StringUtils.isBlank( keyword ) ) {
        continue;
      }
      keyword = keyword.trim();
      if ( strBuf == null ) {
        strBuf = new StringBuffer();
      } else {
        strBuf.append( " " + sqlCondition + " " );
      }
      strBuf.append( "( " + sqlField + " " + ( sqlLike ? "LIKE" : "NOT LIKE" )
          + " '%" + StringEscapeUtils.escapeSql( keyword ) + "%' )" );
    }
    if ( strBuf == null ) {
      return null;
    }
    return strBuf.toString();
  }

}
