package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.neptune.api.requestlater.dao.RequestDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.jpa.JPAStorage;
import com.neptune.api.template.storage.jpa.StorageTemplateJPA;

@JPAStorage
public class RequestJPA extends StorageTemplateJPA<Request>
        implements RequestDAO {

    /**
     * 
     */
    @Inject
    @PersistenceContext(unitName = "requestlater")
    EntityManager em;

    @Inject
    public RequestJPA() {
        super();

        //
        this.getOrders().add(new Ordering(Direction.ASC, "createdOn"));
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
