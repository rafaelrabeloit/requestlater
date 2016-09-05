package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.jpa.JPAStorage;
import com.neptune.api.template.storage.jpa.StorageTemplateJPA;

@JPAStorage
public class ScheduleJPA extends StorageTemplateJPA<Schedule>
        implements ScheduleDAO {

    /**
     * 
     */
    @Inject
    @PersistenceContext(unitName = "requestlater")
    EntityManager em;

    @Inject
    public ScheduleJPA() {
        super();

        //
        this.getOrders().add(new Ordering(Direction.ASC, "createdOn"));
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
