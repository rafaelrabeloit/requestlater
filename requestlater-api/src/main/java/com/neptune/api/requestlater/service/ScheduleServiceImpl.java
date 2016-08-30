package com.neptune.api.requestlater.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.dao.queue.ScheduleQueue;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.template.storage.MemoryStorage;
import com.neptune.queue.DelayedQueue.OnTimeListener;

@Singleton
public class ScheduleServiceImpl extends ServiceTemplateImpl<Schedule>
        implements ScheduleService, OnTimeListener<Schedule> {

    final static Logger logger = LogManager
            .getLogger(ScheduleServiceImpl.class);

    @Inject
    @JPAStorage
    ScheduleDAO persistence;

    @Inject
    @QueueStorage
    ScheduleDAO queue;

    @Inject
    public ScheduleServiceImpl() {
        super();
    }

    @PostConstruct
    public void prepare() {
        ((ScheduleQueue) queue).setOnTimeListener(this);
    }

    @Override
    public Schedule create(Schedule entity) {
        queue.create(entity);
        return super.create(entity);
    }

    @Override
    public Schedule update(Schedule entity) {
        queue.update(entity);
        return super.update(entity);
    }

    @Override
    public Schedule delete(Schedule entity) {
        queue.delete(entity);
        return super.delete(entity);
    }

    @Override
    public ScheduleDAO getDAO() {
        return persistence;
    }

    @Override
    public void onTime(Schedule entity) {
        super.update(entity);
        logger.debug(entity + " finished");
    }

}
