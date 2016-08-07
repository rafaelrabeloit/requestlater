package com.neptune.api.requestdelayer.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.neptune.api.requestdelayer.dao.ValueDAO;
import com.neptune.api.template.dao.HibernateDAO;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.requestdelayer.domain.Value;

@Stateless
public class ValueServiceImpl extends ServiceTemplateImpl<Value>
        implements ValueService {

    @Inject
    @HibernateDAO
    ValueDAO dao;

    @Inject
    public ValueServiceImpl() {
        super();
    }

    @Override
    public ValueDAO getDAO() {
        return dao;
    }

    @Override
    public Value last() {
        return dao.last();
    }

}
