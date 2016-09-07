package com.neptune.api.requestlater.resource;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.requestlater.service.ResponseService;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.resource.ResourceRegex;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/requests/{requestId: " + ResourceRegex.UUID + "}/responses/")
public class ResponseResource extends ResourceTemplate<Response> {

    @Inject
    ResponseService service;

    @PathParam("requestId")
    String requestId;

    @PostConstruct
    public void setFilters() {
        service.getDAO().getFilters()
                .add(new Filtering(Filtering.Operation.EQUAL, "requestId",
                        UUID.fromString(requestId)));
    }

    @Produces("nothing")
    @Override
    public javax.ws.rs.core.Response put(String id, Response entity) {
        return javax.ws.rs.core.Response.status(405).build();
    }

    @Produces("nothing")
    @Override
    public javax.ws.rs.core.Response add(Response entity) {
        return javax.ws.rs.core.Response.status(405).build();
    }
    
    @Override
    public ResponseService getService() {
        return service;
    }
}