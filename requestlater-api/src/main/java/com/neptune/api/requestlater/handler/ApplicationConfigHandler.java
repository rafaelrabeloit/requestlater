package com.neptune.api.requestlater.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class ApplicationConfigHandler implements ApplicationEventListener {
    final static Logger logger = LogManager
            .getLogger(ApplicationConfigHandler.class);

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
        case INITIALIZATION_FINISHED:

            logger.info("Application "
                    + event.getResourceConfig().getApplicationName()
                    + " init done.");

            break;
        case DESTROY_FINISHED:

            // End C3P0 threads
            for (Object o : C3P0Registry.getPooledDataSources()) {
                try {
                    ((PooledDataSource) o).close();
                } catch (Exception e) {
                    // oh well, let tomcat do the complaing for us.
                }
            }

            // Shutdown JDBC Mysql Thread
            try {
                AbandonedConnectionCleanupThread.shutdown();
            } catch (InterruptedException e) {
                logger.error("SEVERE problem cleaning up.", e);
            }

            logger.info("Application "
                    + event.getResourceConfig().getApplicationName()
                    + " terminated");
            break;
        default:
            break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}