package com.beepcast.subscriber;

import org.apache.commons.lang.StringUtils;

public class SubscriberGroupReportParamSupport {

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ///////////////////////////////////////////////////////////////////////////

  public static final String DELIMITER = ".";

  // FLDHDR = Field Header

  public static final String FLDHDR_COUNTRYTOTALALLNUMBERS = "CTAN";
  public static final String FLDHDR_COUNTRYTOTALSUBSCRIBEDNUMBERS = "CTSN";
  public static final String FLDHDR_COUNTRYTOTALUNIQUENUMBERS = "CTUN";
  public static final String FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUENUMBERS = "CTVN";

  public static final String FLDHDR_COUNTRYTOTALALLSCHEDULED = "CTAS";
  public static final String FLDHDR_COUNTRYTOTALSUBSCRIBEDSCHEDULED = "CTSS";
  public static final String FLDHDR_COUNTRYTOTALUNIQUESCHEDULED = "CTUS";
  public static final String FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUESCHEDULED = "CTVS";

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ///////////////////////////////////////////////////////////////////////////

  public static String getCountryCode( String countryTotalNumbersField ) {
    return getFieldSubHeader( countryTotalNumbersField );
  }

  // ///////////////////////////////////////////////////////////////////////////

  public static boolean addParamCountryTotalAllNumbers(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalAllNumbersField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalSubscribedNumbers(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalSubscribedNumbersField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalUniqueNumbers(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalUniqueNumbersField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalSubscribedUniqueNumbers(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalSubscribedUniqueNumbersField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  // ///////////////////////////////////////////////////////////////////////////

  public static boolean addParamCountryTotalAllScheduled(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalAllScheduledField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalSubscribedScheduled(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalSubscribedScheduledField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalUniqueScheduled(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalUniqueScheduledField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  public static boolean addParamCountryTotalSubscribedUniqueScheduled(
      SubscriberGroupReportBean sgrBean , String countryCode , int totalNumbers ) {
    boolean result = false;
    if ( sgrBean == null ) {
      return result;
    }
    String field = createCountryTotalSubscribedUniqueScheduledField( countryCode );
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    sgrBean.addParam( field , Integer.toString( totalNumbers ) );
    result = true;
    return result;
  }

  // ///////////////////////////////////////////////////////////////////////////

  public static String createCountryTotalAllNumbersField( String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALALLNUMBERS , countryCode );
  }

  public static String createCountryTotalSubscribedNumbersField(
      String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALSUBSCRIBEDNUMBERS , countryCode );
  }

  public static String createCountryTotalUniqueNumbersField( String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALUNIQUENUMBERS , countryCode );
  }

  public static String createCountryTotalSubscribedUniqueNumbersField(
      String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUENUMBERS ,
        countryCode );
  }

  // ///////////////////////////////////////////////////////////////////////////

  public static String createCountryTotalAllScheduledField( String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALALLSCHEDULED , countryCode );
  }

  public static String createCountryTotalSubscribedScheduledField(
      String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALSUBSCRIBEDSCHEDULED , countryCode );
  }

  public static String createCountryTotalUniqueScheduledField(
      String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALUNIQUESCHEDULED , countryCode );
  }

  public static String createCountryTotalSubscribedUniqueScheduledField(
      String countryCode ) {
    if ( StringUtils.isBlank( countryCode ) ) {
      return null;
    }
    return createField( FLDHDR_COUNTRYTOTALSUBSCRIBEDUNIQUESCHEDULED ,
        countryCode );
  }

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ///////////////////////////////////////////////////////////////////////////

  private static final String getFieldHeader( String field ) {
    String result = null;
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    String[] arr = StringUtils.split( field , DELIMITER );
    if ( ( arr == null ) || ( arr.length < 2 ) ) {
      return result;
    }
    result = StringUtils.trimToEmpty( arr[0] );
    return result;
  }

  private static final String getFieldSubHeader( String field ) {
    String result = null;
    if ( StringUtils.isBlank( field ) ) {
      return result;
    }
    String[] arr = StringUtils.split( field , DELIMITER );
    if ( ( arr == null ) || ( arr.length < 2 ) ) {
      return result;
    }
    result = StringUtils.trimToEmpty( arr[1] );
    return result;
  }

  private static final String createField( String fieldHeader ,
      String fieldSubHeader ) {
    if ( StringUtils.isBlank( fieldHeader ) ) {
      return null;
    }
    if ( StringUtils.isBlank( fieldSubHeader ) ) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    sb.append( fieldHeader );
    sb.append( DELIMITER );
    sb.append( fieldSubHeader );
    return sb.toString();
  }

}
