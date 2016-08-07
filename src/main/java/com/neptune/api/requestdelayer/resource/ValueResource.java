package com.neptune.api.requestdelayer.resource;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.neptune.api.template.dao.Filter;
import com.neptune.api.template.resource.ResourceTemplate;
import com.neptune.api.requestdelayer.domain.Value;
import com.neptune.api.requestdelayer.service.ValueService;

@Path("/values")
public class ValueResource extends ResourceTemplate<Value> {

	@Inject
	ValueService service;
	
    // @PathParam("watcherId")
    // String watcherId;
	
	@PostConstruct
	public void setFilters() {
//		LinkedList<Filter> filters = new LinkedList<>(); 
//		filters.add(new Filter(Filter.Operation.EQUAL, "watcherId", watcherId));
//		
//		service.getDAO().getFilters().addAll(filters);
	}
	
	@Override
	public ValueService getService() {
		return service;
	}
	
	@GET
	@Path("/last")
	public Response last() {
		Value last = this.service.last();
    	return Response.status(Status.OK).entity(last).build();
	}
}