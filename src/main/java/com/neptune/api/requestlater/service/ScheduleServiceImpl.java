package com.neptune.api.requestlater.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.dao.queue.QueueStorage;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;

@RequestScoped
public class ScheduleServiceImpl extends ServiceTemplateImpl<Schedule>
        implements ScheduleService {

    @Inject
    @QueueStorage
    ScheduleDAO dao;

    @Inject
    public ScheduleServiceImpl() {
        super();
    }

    @Override
    public ScheduleDAO getDAO() {
        return dao;
    }

}
