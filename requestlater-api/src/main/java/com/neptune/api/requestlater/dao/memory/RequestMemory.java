package com.neptune.api.requestlater.dao.memory;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.storage.MemoryStorage;
import com.neptune.api.template.storage.StorageTemplateMemory;

@MemoryStorage
public class RequestMemory extends StorageTemplateMemory<Request>
        implements RequestDAO {

    @Inject
    @MemoryStorage
    ResponseDAO responses;

    @Inject
    public RequestMemory() {
        super();
    }

    @Override
    public Request update(Request entity) {
        // Update children
        for (Response r : entity.getResponses()) {
            try {
                if (r.getId() != null) {
                    responses.update(r);
                } else {
                    throw new NoSuchElementException();
                }
            } catch (NoSuchElementException e) {
                responses.create(r);
            }
        }
        
        // Merge old instance relationship with new one (new one will persist)
        Request req = this.retrieve(entity);
        for (Response resp : req.getResponses()) {
            entity.getResponses().add(resp);
        }
        
        return super.update(entity);
    }
    
    @Override
    public Request create(Request entity) {
        boolean add = entity.getSchedule().getRequests().contains(entity);
        
        if (add) {
            entity.getSchedule().getRequests().add(entity);
        }
        
        return super.create(entity);
    }
    
    @Override
    public Request delete(Request entity) {
        Request element = super.delete(entity);
        element.getSchedule().getRequests().remove(entity);
        //TODO: remove all responses associated
        return element;
    }
}
