package com.neptune.api.requestlater.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;

@Stateless
public class ScheduleServiceImpl extends ServiceTemplateImpl<Request>
        implements ScheduleService {

    @Inject
    @JPAStorage
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
