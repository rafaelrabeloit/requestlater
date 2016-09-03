package com.neptune.api.requestlater;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.neptune.api.requestlater.filter.CORSResponseFilter;
import com.neptune.api.requestlater.handler.ApplicationEventHandler;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    private static EntityManagerFactory FACTORY = Persistence
            .createEntityManagerFactory("requestlater");

    public static EntityManager newEntityManager() {
        return FACTORY.createEntityManager();
    }

    public ApplicationConfig() {
        super();
        this.packages("com.neptune.api.template;com.neptune.api.requestlater;")
                .register(ApplicationEventHandler.class)
                .register(CORSResponseFilter.class)
                // .register(BasicAuthFilter.class)
                .register(DeclarativeLinkingFeature.class)
                .register(JacksonFeature.class)
//                .register(JacksonContextResolver.class)
                .setApplicationName("RequestLater");
    }
}