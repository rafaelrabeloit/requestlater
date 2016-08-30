package com.neptune.api.requestlater.resource;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.requestlater.service.ScheduleService;
import com.neptune.api.template.resource.ResourceTemplate;

@Path("/schedules")
public class ScheduleResource extends ResourceTemplate<Schedule> {

    @Inject
    ScheduleService service;

    @Override
    public ScheduleService getService() {
        return service;
    }
}