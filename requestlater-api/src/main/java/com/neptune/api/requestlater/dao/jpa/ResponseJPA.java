package com.neptune.api.requestlater.dao.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.neptune.api.requestlater.ApplicationConfig;
import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.JPAStorage;
import com.neptune.api.template.storage.StorageTemplateJPA;

@JPAStorage
public class ResponseJPA extends StorageTemplateJPA<Response>
        implements ResponseDAO {

    /**
     * 
     */
    private EntityManager em;

    @Inject
    public ResponseJPA() {
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
