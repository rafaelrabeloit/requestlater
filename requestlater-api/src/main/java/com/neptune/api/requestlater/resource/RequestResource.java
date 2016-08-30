package com.neptune.api.requestlater.resource;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.service.RequestService;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/requests")
public class RequestResource extends ResourceTemplate<Request> {

    @Inject
    RequestService service;

    @Override
    public RequestService getService() {
        return service;
    }
}