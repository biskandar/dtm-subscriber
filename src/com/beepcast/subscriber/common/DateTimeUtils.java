package com.beepcast.subscriber.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtils {

  private static final String SQL_DTM_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static Calendar calendarNowEmptyTime() {
    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.HOUR_OF_DAY , 0 );
    cal.set( Calendar.MINUTE , 0 );
    cal.set( Calendar.SECOND , 0 );
    cal.set( Calendar.MILLISECOND , 0 );
    return cal;
  }

  public static Calendar calendarNowEmptySeconds() {
    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.SECOND , 0 );
    cal.set( Calendar.MILLISECOND , 0 );
    return cal;
  }

  public static Calendar calendarDaysBefore( int nDays ) {
    Calendar cal = calendarNowEmptySeconds();
    if ( nDays > 0 ) {
      cal.add( Calendar.DAY_OF_MONTH , nDays * -1 );
    }
    return cal;
  }

  public static String sqlDtmDaysBefore( int nDays ) {
    String dtm = null;
    Calendar cal = calendarDaysBefore( nDays );
    SimpleDateFormat sdf = new SimpleDateFormat( SQL_DTM_FORMAT );
    dtm = sdf.format( cal.getTime() );
    return dtm;
  }

}
