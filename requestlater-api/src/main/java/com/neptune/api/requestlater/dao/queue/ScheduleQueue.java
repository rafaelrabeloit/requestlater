package com.neptune.api.requestlater.dao.queue;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.GenericEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.dao.DAOTemplateImpl;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.dao.Ordering;
import com.neptune.queue.DelayedQueue;
import com.neptune.queue.DelayedQueue.OnTimeListener;

@QueueStorage
@Singleton
public class ScheduleQueue extends DAOTemplateImpl<Schedule>
        implements ScheduleDAO {
    final static Logger LOGGER = LogManager
            .getLogger(ScheduleQueue.class);

    /**
     * Queue used for this elements
     */
    private DelayedQueue<Schedule> queue;

    @Inject
    public ScheduleQueue() {
        super();
        queue = new DelayedQueue<>();
    }

    public void stop() {
        try {
            this.queue.stop();
        } catch (InterruptedException e) {
            LOGGER.error("Queue Stop was Interrupted!", e);
        }
    }
    
    public void setOnTimeListener(OnTimeListener<Schedule> listener) {
        this.queue.setOnTimeListener(listener);
    }
    
    @Override
    public GenericEntity<List<Schedule>> page(Integer maxResults,
            Integer offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schedule create(Schedule entity) {
        if (entity.getActive())
        queue.add(entity);
        return entity;
    }

    @Override
    public Schedule retrieve(Schedule entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schedule update(Schedule entity) {
        if (queue.remove(entity) && entity.getActive()) {
            queue.add(entity);
            return entity;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Schedule delete(Schedule entity) {
        if (queue.remove(entity)) {
            return entity;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<Filtering> getFilters() {
        return null;
    }

    @Override
    public List<Ordering> getOrders() {
        return null;
    }

}
