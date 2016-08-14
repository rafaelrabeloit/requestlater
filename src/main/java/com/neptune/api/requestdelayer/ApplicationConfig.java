package com.neptune.api.requestdelayer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.neptune.api.requestdelayer.filter.CORSResponseFilter;
import com.neptune.api.requestdelayer.handler.ApplicationEventHandler;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    private static EntityManagerFactory FACTORY = Persistence
            .createEntityManagerFactory("requestdelayer");

    public static EntityManagerFactory getFactory() {
        return FACTORY;
    }

    public static EntityManager newEntityManager() {
        return FACTORY.createEntityManager();
    }
    
    public ApplicationConfig() {
        super();
        this.packages(
                "com.neptune.api.template;com.neptune.api.requestdelayer;")
                .register(DeclarativeLinkingFeature.class)
                .register(ApplicationEventHandler.class)
                .register(CORSResponseFilter.class)
                .register(JacksonFeature.class)
                // .register(BasicAuthFilter.class)
                .setApplicationName("RequestDelayer");
    }
}