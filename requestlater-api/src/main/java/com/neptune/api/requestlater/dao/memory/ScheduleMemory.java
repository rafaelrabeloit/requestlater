package com.neptune.api.requestlater.dao.memory;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.storage.MemoryStorage;
import com.neptune.api.template.storage.StorageTemplateMemory;

@MemoryStorage
public class ScheduleMemory extends StorageTemplateMemory<Schedule>
        implements ScheduleDAO {

    @Inject
    @MemoryStorage
    RequestDAO requests;

    @Inject
    public ScheduleMemory() {
        super();
    }

    @Override
    public Schedule update(Schedule entity) {
        // Update children
        for (Request r : entity.getRequests()) {
            try {
                if (r.getId() != null) {
                    requests.update(r);
                } else {
                    throw new NoSuchElementException();
                }
            } catch (NoSuchElementException e) {
                requests.create(r);
            }
        }
        
        // Merge old instance relationship with new one (new one will persist)
        Schedule s = this.retrieve(entity);
        for (Request r : s.getRequests()) {
            entity.getRequests().add(r);
        }
        
        return super.update(entity);
    }

    @Override
    public Schedule delete(Schedule entity) {
        // TODO remove all requests associated
        return super.delete(entity);
    }
}
