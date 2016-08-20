package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.neptune.api.requestlater.ApplicationConfig;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Schedule;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.template.storage.StorageTemplateJPA;

@JPAStorage
public class ScheduleJPA extends StorageTemplateJPA<Schedule>
        implements ScheduleDAO {

    /**
     * 
     */
    private EntityManager em;

    @Inject
    public ScheduleJPA() {
        super();

        //
        this.getOrders().add(new Ordering(Direction.ASC, "createdOn"));

        //
        this.em = ApplicationConfig.newEntityManager();
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
