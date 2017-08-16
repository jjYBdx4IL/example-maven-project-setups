package com.github.jjYBdx4IL.maven.examples.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ConfigDumpListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigDumpListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
