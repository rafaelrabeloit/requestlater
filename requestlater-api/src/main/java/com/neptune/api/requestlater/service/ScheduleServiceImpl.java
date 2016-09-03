package com.neptune.api.requestlater.service;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;

public class ScheduleServiceImpl extends ServiceTemplateImpl<Schedule>
        implements ScheduleService {

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

    @Override
    public Schedule create(Schedule entity) {
        // TODO: maybe I should change the order here so when OnTime will have a
        // sync object
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

}
