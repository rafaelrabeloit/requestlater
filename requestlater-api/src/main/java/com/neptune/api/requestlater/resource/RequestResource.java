package com.neptune.api.requestlater.resource;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.requestlater.service.RequestService;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.resource.ResourceRegex;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/schedules/{scheduleId: " + ResourceRegex.UUID + "}/requests/")
public class RequestResource extends ResourceTemplate<Request> {

    @Inject
    RequestService service;

    @PathParam("scheduleId")
    String scheduleId;

    @PostConstruct
    public void setFilters() {
        service.getDAO().getFilters()
                .add(new Filtering(Filtering.Operation.EQUAL, "scheduleId",
                        UUID.fromString(scheduleId)));
    }

    @Override
    public Response add(Request entity) {
        entity.setSchedule(new Schedule(UUID.fromString(scheduleId)));
        return super.add(entity);
    }

    @Override
    public RequestService getService() {
        return service;
    }
}