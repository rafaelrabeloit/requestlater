package com.neptune.api.requestlater.handler;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import com.neptune.api.requestlater.dao.jpa.ScheduleJPA;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.dao.queue.ScheduleQueue;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.queue.DelayedQueue.OnTimeListener;

public class QueueConfigHandler implements ApplicationEventListener {
    static final Logger LOGGER = LogManager.getLogger(QueueConfigHandler.class);

    @Inject
    @PersistenceUnit(unitName = "requestlater")
    EntityManagerFactory jpaFactory;

    @Inject
    @QueueStorage
    ScheduleQueue queue;

    ScheduleJPA schedules = new ScheduleJPA();

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
        case INITIALIZATION_FINISHED:

            // Set persistence manager for DAO
            schedules.setEntityManager(jpaFactory.createEntityManager());

            // Set Listener for Queue
            queue.setOnTimeListener(new OnTimeListener<Schedule>() {
                @Override
                public void onTime(Schedule e) {
                    // try to update on queue, in case run choose another time
                    // for it
                    queue.update(e);
                    // update on db
                    schedules.update(e);
                    
                    LOGGER.debug("Processing of " + e + " finished");
                }

                @Override
                public Schedule onBeforeRun(Schedule e) {
                    Schedule s = schedules.retrieve(e);
                    return s;
                }
            });

            break;
        case DESTROY_FINISHED:
            // Stop Queue
            queue.stop();

            // close persistence manager
            schedules.getEntityManager().close();

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