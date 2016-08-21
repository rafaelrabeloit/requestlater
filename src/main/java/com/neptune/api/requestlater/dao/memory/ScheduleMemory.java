package com.neptune.api.requestlater.dao.memory;

import java.util.ArrayList;
import java.util.HashSet;
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

@MemoryStorage
@Singleton
public class ScheduleMemory extends DAOTemplateImpl<Schedule>
        implements ScheduleDAO {

    /**
     * Queue used for this elements
     */
    private HashSet<Schedule> mem;

    @Inject
    public ScheduleMemory() {
        super();
        mem = new HashSet<>();
    }

    @Override
    public GenericEntity<List<Schedule>> page(Integer maxResults,
            Integer offset) {
        // Transforms queue in List
        List<Schedule> all = new ArrayList<>(mem);

        // check boundaries to the sublist
        int to = Math.min(offset + maxResults, all.size());

        List<Schedule> selection = all.subList(offset, to);

        // generate a page from a sublist
        return new GenericEntity<List<Schedule>>(selection, getType());
    }

    @Override
    public Schedule create(Schedule entity) {
        mem.add(entity);
        return entity;
    }

    @Override
    public Schedule retrieve(Schedule entity) {
        Schedule element = null;

        for (Schedule i : mem) {
            if (i.equals(entity)) {
                element = i;
                break;
            }
        }

        return element;
    }

    @Override
    public Schedule update(Schedule entity) {
        Schedule original = this.retrieve(entity);
        if (original != null) {
            original.copy(entity);
            return entity;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Schedule delete(Schedule entity) {
        if (mem.remove(entity)) {
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
