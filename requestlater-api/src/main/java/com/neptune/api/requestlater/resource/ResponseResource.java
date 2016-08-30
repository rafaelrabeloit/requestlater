package com.neptune.api.requestlater.resource;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.requestlater.service.ResponseService;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/responses")
public class ResponseResource extends ResourceTemplate<Response> {

    @Inject
    ResponseService service;

    @Override
    public ResponseService getService() {
        return service;
    }
}