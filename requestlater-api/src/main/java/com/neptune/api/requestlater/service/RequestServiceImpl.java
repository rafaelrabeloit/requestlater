package com.neptune.api.requestlater.service;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.template.storage.MemoryStorage;

public class RequestServiceImpl extends ServiceTemplateImpl<Request>
        implements RequestService {

    @Inject
    @JPAStorage
    RequestDAO persistence;

    @Inject
    @JPAStorage
    ScheduleDAO schedules;

    @Inject
    public RequestServiceImpl() {
        super();
    }

    @Override
    public Request create(Request entity) {
        Schedule schedule = schedules
                .retrieve(new Schedule(entity.getScheduleId()));
        entity.setSchedule(schedule);
        return super.create(entity);
    }

    @Override
    public RequestDAO getDAO() {
        return persistence;
    }

}
