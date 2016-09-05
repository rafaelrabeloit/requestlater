package com.neptune.api.requestlater.service;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.jpa.JPAStorage;

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
        return super.create(entity);
    }

    @Override
    public RequestDAO getDAO() {
        return persistence;
    }

}
