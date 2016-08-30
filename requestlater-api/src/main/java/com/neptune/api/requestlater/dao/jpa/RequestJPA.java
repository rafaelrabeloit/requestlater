package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.neptune.api.requestlater.ApplicationConfig;
import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.template.storage.StorageTemplateJPA;

@JPAStorage
public class RequestJPA extends StorageTemplateJPA<Request>
        implements RequestDAO {

    /**
     * 
     */
    private EntityManager em;

    @Inject
    public RequestJPA() {
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
