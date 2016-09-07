package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.jpa.JPAStorage;
import com.neptune.api.template.storage.jpa.StorageTemplateJPA;

@JPAStorage
public class ResponseJPA extends StorageTemplateJPA<Response>
        implements ResponseDAO {

    /**
     * 
     */
    @Inject
    @PersistenceContext(unitName = "requestlater")
    EntityManager em;

    @Inject
    public ResponseJPA() {
        super();

        //
        this.getOrders().add(new Ordering(Direction.DESC, "createdOn"));
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
