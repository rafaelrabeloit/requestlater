package com.neptune.api.requestlater.handler;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.PooledDataSource;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.dao.queue.ScheduleQueue;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.queue.DelayedQueue.OnTimeListener;

public class ApplicationEventHandler implements ApplicationEventListener {
    final static Logger logger = LogManager
            .getLogger(ApplicationEventHandler.class);

    @Inject
    @QueueStorage
    ScheduleDAO queue;
    
    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
        case INITIALIZATION_FINISHED:

            // Set Listener for Queue
            ((ScheduleQueue) queue).setOnTimeListener(new OnTimeListener<Schedule>() {
                @Override
                public void onTime(Schedule e) {
                    queue.update(e);
                    logger.debug(e + " finished");
                }
            });
            
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
            
            //Stop Queue
            ((ScheduleQueue) queue).stop();
            
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