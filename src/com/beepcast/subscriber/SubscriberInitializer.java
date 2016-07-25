package com.beepcast.subscriber;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.beepcast.util.properties.GlobalEnvironment;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class SubscriberInitializer implements ServletContextListener {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static final String PROPERTY_FILE_SUBSCRIBER = "subscriber.config.file";

  static final DLogContext lctx = new SimpleContext( "SubscriberInitializer" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void contextInitialized( ServletContextEvent sce ) {

    ServletContext context = sce.getServletContext();
    String logStr = "";

    GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

    SubscriberConf subscriberConf = SubscriberConfFactory
        .generateSubscriberConf( PROPERTY_FILE_SUBSCRIBER );
    logStr = this.getClass() + " : initialized " + subscriberConf;
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

    SubscriberApp subscriberApp = SubscriberApp.getInstance();
    subscriberApp.init( subscriberConf );
    subscriberApp.moduleStart();
    logStr = this.getClass() + " : initialized " + subscriberApp;
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

  }

  public void contextDestroyed( ServletContextEvent sce ) {

    ServletContext context = sce.getServletContext();
    String logStr = "";

    GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

    SubscriberApp subscriberApp = SubscriberApp.getInstance();
    subscriberApp.moduleStop();
    logStr = this.getClass() + " : destroyed ";
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

  }

}
