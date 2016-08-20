package com.neptune.api.requestlater.dao.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.GenericEntity;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.dao.DAOTemplateImpl;
import com.neptune.api.template.dao.Filtering;
import com.neptune.api.template.dao.Ordering;
import com.neptune.queue.DelayedQueue;

@QueueStorage
@Singleton
public class ScheduleQueue extends DAOTemplateImpl<Schedule>
        implements ScheduleDAO {

    /**
     * Queue used for this elements
     */
    private DelayedQueue<Schedule> queue;

    @Inject
    public ScheduleQueue() {
        super();
        queue = new DelayedQueue<>();
    }

    @Override
    public GenericEntity<List<Schedule>> page(Integer maxResults,
            Integer offset) {
        // Transforms queue in List
        List<Schedule> all = new ArrayList<>(queue);

        // check boundaries to the sublist
        int to = Math.min(offset + maxResults, all.size());

        List<Schedule> selection = all.subList(offset, to);

        // generate a page from a sublist
        return new GenericEntity<List<Schedule>>(selection, getType());
    }

    @Override
    public Schedule create(Schedule entity) {
        queue.add(entity);
        return entity;
    }

    @Override
    public Schedule retrieve(Schedule entity) {
        Schedule element = null;

        for (Schedule i : queue) {
            if (i.equals(entity)) {
                element = i;
                break;
            }
        }

        return element;
    }

    @Override
    public Schedule update(Schedule entity) {
        if (queue.remove(entity)) {
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
