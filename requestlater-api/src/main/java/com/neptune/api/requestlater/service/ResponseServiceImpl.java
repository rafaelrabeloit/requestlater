package com.neptune.api.requestlater.service;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.service.ServiceTemplateImpl;
import com.neptune.api.template.storage.JPAStorage;

public class ResponseServiceImpl extends ServiceTemplateImpl<Response>
        implements ResponseService {

    @Inject
    @JPAStorage
    ResponseDAO persistence;

    @Inject
    public ResponseServiceImpl() {
        super();
    }

    @Override
    public ResponseDAO getDAO() {
        return persistence;
    }

}
