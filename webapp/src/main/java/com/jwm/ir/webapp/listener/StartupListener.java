package com.jwm.ir.webapp.listener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;

/**
 * Responsible for any tasks upon startup of the web application.
 * Created by Jeff on 2016-08-15.
 */
public class StartupListener implements javax.servlet.ServletContextListener {

    private static final Logger log = LogManager.getLogger(StartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Application startup");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("Application shutdown");
    }
}
