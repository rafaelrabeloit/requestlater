package com.neptune.api.requestlater.dao;

import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.dao.DAOTemplate;

public interface ResponseDAO extends DAOTemplate<Response> {

    Response last();

}