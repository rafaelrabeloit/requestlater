package com.neptune.api.requestdelayer.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class ApplicationEventHandler implements ApplicationEventListener {
    final static Logger logger = LogManager
            .getLogger(ApplicationEventHandler.class);

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
        case INITIALIZATION_FINISHED:
            logger.info("Application "
                    + event.getResourceConfig().getApplicationName()
                    + " initializing...");

            logger.info("Done.");
            break;
        case DESTROY_FINISHED:
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