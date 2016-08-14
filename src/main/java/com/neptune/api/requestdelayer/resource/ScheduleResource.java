package com.neptune.api.requestdelayer.resource;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.neptune.api.requestdelayer.domain.Request;
import com.neptune.api.requestdelayer.service.ScheduleService;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/schedules")
public class ScheduleResource extends ResourceTemplate<Request> {

    @Inject
    ScheduleService service;

    @Override
    public ScheduleService getService() {
        return service;
    }

}