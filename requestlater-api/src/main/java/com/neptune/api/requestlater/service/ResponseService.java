package com.neptune.api.requestlater.service;

import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.service.ServiceTemplate;

public interface ResponseService extends ServiceTemplate<Response> {

    Response last();

}