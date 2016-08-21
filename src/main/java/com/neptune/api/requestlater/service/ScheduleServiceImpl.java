package com.neptune.api.requestlater.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.memory.MemoryStorage;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.queue.DelayedQueue.OnTimeListener;

@RequestScoped
public class ScheduleServiceImpl extends ServiceTemplateImpl<Schedule>
        implements ScheduleService, OnTimeListener<Schedule> {

    @Inject
    @MemoryStorage
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
    }

}
