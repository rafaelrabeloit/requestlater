package com.neptune.api.requestlater.dao.memory;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.storage.memory.MemoryStorage;
import com.neptune.api.template.storage.memory.StorageTemplateMemory;

@MemoryStorage
public class ResponseMemory extends StorageTemplateMemory<Response>
        implements ResponseDAO {

    @Inject
    public ResponseMemory() {
        super();
    }

    @Override
    public Response create(Response entity) {
        boolean add = entity.getRequest().getResponses().contains(entity);

        if (add) {
            entity.getRequest().getResponses().add(entity);
        }

        return super.create(entity);
    }

    @Override
    public Response delete(Response entity) {
        Response element = super.delete(entity);
        element.getRequest().getResponses().remove(entity);
        return element;
    }
}
