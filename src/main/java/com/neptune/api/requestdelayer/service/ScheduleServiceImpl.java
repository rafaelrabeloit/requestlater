package com.neptune.api.requestdelayer.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.neptune.api.requestdelayer.dao.ScheduleDAO;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.requestdelayer.domain.Request;

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
