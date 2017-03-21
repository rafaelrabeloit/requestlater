package com.neptune.api.requestlater.service;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.jpa.JPAStorage;

public class ScheduleServiceImpl extends ServiceTemplateImpl<Schedule>
        implements ScheduleService {

    static final Logger LOGGER = LogManager
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
        queue.create(entity);
        return super.create(entity);
    }

    @Override
    public Schedule update(Schedule entity) {
        try {
            queue.update(entity);
        } catch (NoSuchElementException e) {
            LOGGER.debug("Element " + entity + " is not on queue");
        }
        return super.update(entity);
    }

    @Override
    public Schedule delete(Schedule entity) {
        try {
            queue.delete(entity);
        } catch (NoSuchElementException e) {
            LOGGER.debug("Element " + entity + " is not on queue");
        }
        return super.delete(entity);
    }

    @Override
    public ScheduleDAO getDAO() {
        return persistence;
    }

}
