package com.neptune.api.requestdelayer.dao;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.neptune.api.template.dao.Order;
import com.neptune.api.template.dao.Order.Direction;
import com.neptune.api.template.dao.DAOTemplateHibernate;
import com.neptune.api.template.dao.HibernateDAO;
import com.neptune.api.template.dao.util.HibernateUtil;
import com.neptune.api.requestdelayer.domain.Value;

@HibernateDAO
public class ValueDAOImpl extends DAOTemplateHibernate<Value>
        implements ValueDAO {

    @Inject
    public ValueDAOImpl() {
        super();
        this.getOrders().add(new Order(Direction.DESC, "createdOn"));
    }

    @Override
    public Value last() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria = session.createCriteria(getPersistentClass())
                .setMaxResults(1);

        this.composeCriteria(criteria);

        Value entity = (Value) criteria.uniqueResult();

        session.close();

        return entity;
    }

}
