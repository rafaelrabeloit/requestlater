package com.neptune.api.requestlater.dao.jpa;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.dao.ResponseDAO;
import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.template.dao.Ordering;
import com.neptune.api.template.dao.Ordering.Direction;
import com.neptune.api.template.storage.jpa.JPAStorage;
import com.neptune.api.template.storage.jpa.StorageTemplateJPA;

@JPAStorage
public class ResponseJPA extends StorageTemplateJPA<Response>
        implements ResponseDAO {

    static final Logger LOGGER = LogManager.getLogger(Request.class);

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

    @Override
    public Response last() {
        List<Response> result = this.page(1, 0).getEntity();
        Response returned = null;
        try {
            returned = result.get(0);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.info("There is no last Response for this Request.");
        }
        return returned;
    }

}
