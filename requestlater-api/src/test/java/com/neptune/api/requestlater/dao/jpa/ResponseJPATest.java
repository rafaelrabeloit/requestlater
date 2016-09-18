package com.neptune.api.requestlater.dao.jpa;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.neptune.api.requestlater.domain.Request;
import com.neptune.api.requestlater.domain.Response;
import com.neptune.api.requestlater.domain.Schedule;

public class ResponseJPATest extends Mockito {

    static EntityManagerFactory emf;
    EntityManager em;

    public ResponseJPATest() {
    }

    @BeforeClass
    public static void configure() {
        emf = Persistence.createEntityManagerFactory("requestlater");
    }

    @Before
    public void setUp() {
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
    }

    @AfterClass
    public static void destroy() {
        emf.close();
    }

    @Test
    public void test_last() {

        ScheduleJPA schedules = new ScheduleJPA();
        schedules.em = em;
        RequestJPA requests = new RequestJPA();
        requests.em = em;
        ResponseJPA responses = new ResponseJPA();
        responses.em = em;

        Schedule schedule = new Schedule();
        schedules.create(schedule);

        Request request = new Request();
        request.setSchedule(schedule);
        requests.create(request);

        Response initial;

        initial = new Response();
        initial.setRequest(request);
        responses.create(initial);

        initial = new Response();
        initial.setRequest(request);
        responses.create(initial);

        initial = new Response();
        initial.setRequest(request);
        responses.create(initial);

        Response entity = new Response();
        entity.setRequest(request);
        responses.create(entity);

        Response last = responses.last();

        assertEquals("last() didn`t returned last element inserted", entity,
                last);
    }

}
