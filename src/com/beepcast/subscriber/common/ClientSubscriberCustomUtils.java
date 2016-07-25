package com.beepcast.subscriber.common;

import com.beepcast.subscriber.ClientSubscriberCustomBean;

public class ClientSubscriberCustomUtils {

  public static String getCustomValue( ClientSubscriberCustomBean cscBean ,
      int idx ) {
    String value = null;

    if ( cscBean == null ) {
      return value;
    }

    switch ( idx ) {

    case 0 :
      value = cscBean.getCustom0();
      break;
    case 1 :
      value = cscBean.getCustom1();
      break;
    case 2 :
      value = cscBean.getCustom2();
      break;
    case 3 :
      value = cscBean.getCustom3();
      break;
    case 4 :
      value = cscBean.getCustom4();
      break;
    case 5 :
      value = cscBean.getCustom5();
      break;
    case 6 :
      value = cscBean.getCustom6();
      break;
    case 7 :
      value = cscBean.getCustom7();
      break;
    case 8 :
      value = cscBean.getCustom8();
      break;
    case 9 :
      value = cscBean.getCustom9();
      break;

    case 10 :
      value = cscBean.getCustom10();
      break;
    case 11 :
      value = cscBean.getCustom11();
      break;
    case 12 :
      value = cscBean.getCustom12();
      break;
    case 13 :
      value = cscBean.getCustom13();
      break;
    case 14 :
      value = cscBean.getCustom14();
      break;
    case 15 :
      value = cscBean.getCustom15();
      break;
    case 16 :
      value = cscBean.getCustom16();
      break;
    case 17 :
      value = cscBean.getCustom17();
      break;
    case 18 :
      value = cscBean.getCustom18();
      break;
    case 19 :
      value = cscBean.getCustom19();
      break;

    case 20 :
      value = cscBean.getCustom20();
      break;
    case 21 :
      value = cscBean.getCustom21();
      break;
    case 22 :
      value = cscBean.getCustom22();
      break;
    case 23 :
      value = cscBean.getCustom23();
      break;
    case 24 :
      value = cscBean.getCustom24();
      break;
    case 25 :
      value = cscBean.getCustom25();
      break;
    case 26 :
      value = cscBean.getCustom26();
      break;
    case 27 :
      value = cscBean.getCustom27();
      break;
    case 28 :
      value = cscBean.getCustom28();
      break;
    case 29 :
      value = cscBean.getCustom29();
      break;

    case 30 :
      value = cscBean.getCustom30();
      break;

    }

    return value;
  }

}
