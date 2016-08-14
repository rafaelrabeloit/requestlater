package com.neptune.api.requestdelayer.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.neptune.api.requestdelayer.ApplicationConfig;
import com.neptune.api.requestdelayer.dao.ScheduleDAO;
import com.neptune.api.requestdelayer.domain.Request;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.StorageTemplateJPA;
import com.neptune.api.template.storage.JPAStorage;

@JPAStorage
public class ScheduleJPA extends StorageTemplateJPA<Request> implements ScheduleDAO {

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
