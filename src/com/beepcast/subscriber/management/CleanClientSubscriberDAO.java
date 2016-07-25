package com.beepcast.subscriber.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.beepcast.encrypt.EncryptApp;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class CleanClientSubscriberDAO {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "CleanClientSubscriberDAO" );

  public static final String TBLNAME_UNSUBS = "client_subscriber_unsubs";
  public static final String TBLNAME_INVALID = "client_subscriber_invalid";
  public static final String TBLNAME_DNC = "client_subscriber_dnc";

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;
  private DatabaseLibrary dbLib;
  private final String keyPhoneNumber;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public CleanClientSubscriberDAO( boolean debug ) {

    this.debug = debug;

    dbLib = DatabaseLibrary.getInstance();
    EncryptApp encryptApp = EncryptApp.getInstance();
    keyPhoneNumber = encryptApp.getKeyValue( EncryptApp.KEYNAME_PHONENUMBER );

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List listGroupDuplicatedActiveNumbersForUnsubs( int limitRecords ) {
    return listGroupDuplicatedActiveNumbers( TBLNAME_UNSUBS , limitRecords );
  }

  public List listDuplicatedActiveNumbersForUnsubs( List listGroups ) {
    return listDuplicatedActiveNumbers( TBLNAME_UNSUBS , listGroups );
  }

  public List listDeletedNumbersForUnsubs( List listItems ) {
    return listDeletedNumbers( TBLNAME_UNSUBS , listItems );
  }

  public List listGroupDuplicatedActiveNumbersForInvalid( int limitRecords ) {
    return listGroupDuplicatedActiveNumbers( TBLNAME_INVALID , limitRecords );
  }

  public List listDuplicatedActiveNumbersForInvalid( List listGroups ) {
    return listDuplicatedActiveNumbers( TBLNAME_INVALID , listGroups );
  }

  public List listDeletedNumbersForInvalid( List listItems ) {
    return listDeletedNumbers( TBLNAME_INVALID , listItems );
  }

  public List listGroupDuplicatedActiveNumbersForDnc( int limitRecords ) {
    return listGroupDuplicatedActiveNumbers( TBLNAME_DNC , limitRecords );
  }

  public List listDuplicatedActiveNumbersForDnc( List listGroups ) {
    return listDuplicatedActiveNumbers( TBLNAME_DNC , listGroups );
  }

  public List listDeletedNumbersForDnc( List listItems ) {
    return listDeletedNumbers( TBLNAME_DNC , listItems );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private List listGroupDuplicatedActiveNumbers( String tblName ,
      int limitRecords ) {
    List list = new ArrayList();

    if ( limitRecords < 1 ) {
      return list;
    }

    // compose inner sql
    String sqlInnerSelect = "SELECT csx.client_id , "
        + sqlDecryptPhoneNumber( "csx" )
        + " , COUNT( csx.encrypt_phone ) AS total ";
    String sqlInnerFrom = "FROM " + tblName + " csx ";
    String sqlInnerWhere = "WHERE ( csx.active = 1 ) ";
    String sqlInnerGroup = "GROUP BY csx.client_id , csx.encrypt_phone ";
    String sqlInnerOrder = "ORDER BY csx.client_id DESC , csx.encrypt_phone ASC ";
    String sqlInner = sqlInnerSelect + sqlInnerFrom + sqlInnerWhere
        + sqlInnerGroup + sqlInnerOrder;

    // compose sql
    String sqlSelect = "SELECT csxt.client_id , csxt.phone , csxt.total ";
    String sqlFrom = "FROM ( " + sqlInner + " ) csxt ";
    String sqlWhere = "WHERE ( csxt.total > 1 ) ";
    String sqlLimit = "LIMIT " + limitRecords + " ";
    String sql = sqlSelect + sqlFrom + sqlWhere + sqlLimit;

    // execute sql
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return list;
    }

    // populate records
    String stemp = null;
    DuplicatedClientSubscriber rec = null;
    Iterator it = qr.iterator();
    while ( it.hasNext() ) {
      QueryItem qi = (QueryItem) it.next();
      if ( qi == null ) {
        continue;
      }
      rec = new DuplicatedClientSubscriber();
      stemp = (String) qi.get( 0 ); // client_id
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        try {
          rec.setClientId( Integer.parseInt( stemp ) );
        } catch ( NumberFormatException e ) {
        }
      }
      stemp = (String) qi.get( 1 ); // phone
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        rec.setPhoneNumber( stemp );
      }
      stemp = (String) qi.get( 2 ); // total
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        try {
          rec.setTotalDuplicated( Integer.parseInt( stemp ) );
        } catch ( NumberFormatException e ) {
        }
      }
      list.add( rec );
    }

    return list;
  }

  private List listDuplicatedActiveNumbers( String tblName , List listGroups ) {
    List list = new ArrayList();

    // validate must be params
    if ( ( listGroups == null ) || ( listGroups.size() < 1 ) ) {
      return list;
    }

    // compose sql condition
    int clientIdCur = 0 , clientIdNew = 0;
    String phoneNumberCur = "" , phoneNumberNew = null;
    StringBuffer sbCond1 = null , sbCond2 = null;
    DuplicatedClientSubscriber rec = null;
    Iterator iterGroups = listGroups.iterator();
    while ( iterGroups.hasNext() ) {
      rec = (DuplicatedClientSubscriber) iterGroups.next();
      if ( rec == null ) {
        continue;
      }
      clientIdNew = rec.getClientId();
      if ( clientIdNew < 1 ) {
        continue;
      }
      phoneNumberNew = rec.getPhoneNumber();
      if ( ( phoneNumberNew == null ) || ( phoneNumberNew.equals( "" ) ) ) {
        continue;
      }

      if ( clientIdNew != clientIdCur ) {
        if ( sbCond1 == null ) {
          sbCond1 = new StringBuffer();
        } else {
          if ( sbCond2 != null ) {
            sbCond1.append( sbCond2 );
            sbCond1.append( " ) ) " );
          }
          sbCond1.append( "OR " );
        }
        sbCond2 = null;
        sbCond1.append( "( ( csx.client_id = " );
        sbCond1.append( clientIdNew );
        sbCond1.append( " ) AND ( " );
      }

      if ( sbCond1 != null ) {
        if ( sbCond2 == null ) {
          sbCond2 = new StringBuffer();
        } else {
          sbCond2.append( "OR " );
        }
        sbCond2.append( "( csx.encrypt_phone = " );
        sbCond2.append( sqlEncryptPhoneNumber( phoneNumberNew ) );
        sbCond2.append( ") " );
      }

      clientIdCur = clientIdNew;
      phoneNumberCur = phoneNumberNew;
    }
    if ( sbCond1 != null ) {
      if ( sbCond2 != null ) {
        sbCond1.append( sbCond2 );
      }
      sbCond1.append( " ) ) " );
    } else {
      return list;
    }

    // compose sql
    String sqlSelect = "SELECT csx.id , csx.client_id , "
        + sqlDecryptPhoneNumber( "csx" ) + " ";
    String sqlFrom = "FROM " + tblName + " csx ";
    String sqlWhere = "WHERE " + sbCond1.toString() + "  ";
    String sqlOrder = "ORDER BY csx.client_id DESC ";
    sqlOrder += ", csx.encrypt_phone ASC , csx.id ASC ";
    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder;

    // execute sql
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    QueryResult qr = dbLib.simpleQuery( "profiledb" , sql );
    if ( ( qr == null ) || ( qr.size() < 1 ) ) {
      return list;
    }

    // populate records
    String stemp = null;
    Iterator it = qr.iterator();
    while ( it.hasNext() ) {
      QueryItem qi = (QueryItem) it.next();
      if ( qi == null ) {
        continue;
      }
      rec = new DuplicatedClientSubscriber();
      stemp = (String) qi.get( 0 ); // id
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        try {
          rec.setId( Integer.parseInt( stemp ) );
        } catch ( NumberFormatException e ) {
        }
      }
      stemp = (String) qi.get( 1 ); // client_id
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        try {
          rec.setClientId( Integer.parseInt( stemp ) );
        } catch ( NumberFormatException e ) {
        }
      }
      stemp = (String) qi.get( 2 ); // phone
      if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
        rec.setPhoneNumber( stemp );
      }
      list.add( rec );
    }

    return list;
  }

  private List listDeletedNumbers( String tblName , List listItems ) {
    List list = new ArrayList();

    // validate must be params
    if ( ( listItems == null ) || ( listItems.size() < 1 ) ) {
      return list;
    }

    // compose the list of deleted rec
    int clientIdCur = 0 , clientIdNew , clientIdNxt = 0;
    String phoneNumberCur = "" , phoneNumberNew = null , phoneNumberNxt = null;
    DuplicatedClientSubscriber recNew = null , recNxt = null;
    boolean deleteLastIndex = false;
    int sizeItems = listItems.size();
    for ( int idxItems = 0 ; idxItems < sizeItems ; idxItems++ ) {
      recNew = (DuplicatedClientSubscriber) listItems.get( idxItems );
      if ( recNew == null ) {
        continue;
      }
      clientIdNew = recNew.getClientId();
      phoneNumberNew = recNew.getPhoneNumber();
      if ( ( clientIdNew < 1 ) || ( phoneNumberNew == null )
          || ( phoneNumberNew.equals( "" ) ) ) {
        continue;
      }
      list.add( recNew );
      deleteLastIndex = false;
      if ( ( clientIdNew == clientIdCur )
          && ( phoneNumberNew.equals( phoneNumberCur ) ) ) {
        if ( ( idxItems + 1 ) < sizeItems ) {
          recNxt = (DuplicatedClientSubscriber) listItems.get( idxItems + 1 );
          if ( recNxt != null ) {
            clientIdNxt = recNxt.getClientId();
            phoneNumberNxt = recNxt.getPhoneNumber();
            if ( ( ( clientIdNxt > 0 ) && ( phoneNumberNxt != null ) )
                && ( ( clientIdNxt != clientIdNew ) || ( !phoneNumberNxt
                    .equals( phoneNumberNew ) ) ) ) {
              deleteLastIndex = true;
            }
          }
        } else {
          deleteLastIndex = true;
        }
      }
      if ( deleteLastIndex ) {
        if ( list.size() > 0 ) {
          list.remove( list.size() - 1 );
        }
      }
      clientIdCur = clientIdNew;
      phoneNumberCur = phoneNumberNew;
    } // while ( iterItems.hasNext() )
    if ( list.size() < 1 ) {
      return list;
    }

    // compose sql based on list deleted rec

    StringBuffer sbIds = null;
    DuplicatedClientSubscriber rec = null;
    Iterator iterItems = list.iterator();
    while ( iterItems.hasNext() ) {
      rec = (DuplicatedClientSubscriber) iterItems.next();
      if ( rec == null ) {
        continue;
      }
      if ( rec.getId() < 1 ) {
        continue;
      }
      if ( sbIds == null ) {
        sbIds = new StringBuffer();
      } else {
        sbIds.append( "," );
      }
      sbIds.append( rec.getId() );
    }
    if ( sbIds == null ) {
      return list;
    }

    String sqlUpdate = "UPDATE " + tblName + " ";
    String sqlSet = "SET active = 0 , date_updated = NOW() ";
    String sqlWhere = "WHERE id IN ( " + sbIds.toString() + " ) ";
    String sql = sqlUpdate + sqlSet + sqlWhere;

    // execute sql
    if ( debug ) {
      DLog.debug( lctx , "Perform " + sql );
    }
    Integer intr = dbLib.executeQuery( "profiledb" , sql );
    if ( intr != null ) {
      DLog.debug( lctx , "Deleted total " + intr.intValue() + " record(s)" );
    }

    return list;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Util Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private String sqlEncryptPhoneNumber( String phoneNumber ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_ENCRYPT('" );
    sb.append( phoneNumber );
    sb.append( "','" );
    sb.append( keyPhoneNumber );
    sb.append( "') " );
    return sb.toString();
  }

  private String sqlDecryptPhoneNumber( String tblAlias ) {
    StringBuffer sb = new StringBuffer();
    sb.append( "AES_DECRYPT(" );
    sb.append( tblAlias );
    sb.append( ".encrypt_phone,'" );
    sb.append( keyPhoneNumber );
    sb.append( "') AS phone" );
    return sb.toString();
  }

}
