package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.neptune.api.requestlater.ApplicationConfig;
import com.neptune.api.requestlater.dao.ScheduleDAO;
import com.neptune.api.requestlater.domain.Request;
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
