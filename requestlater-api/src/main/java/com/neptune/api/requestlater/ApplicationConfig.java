package com.neptune.api.requestlater;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.neptune.api.requestlater.filter.CORSResponseFilter;
import com.neptune.api.requestlater.handler.ApplicationConfigHandler;
import com.neptune.api.requestlater.handler.QueueConfigHandler;

/**
 * Configuration class for Jersey
 * 
 * @author Rafael R. Itajuba
 */
@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    /**
     * Configure application with:
     *  - default packages
     *  - queue handler, because it is singleton
     *  - CORS filter, because it is a public service
     *  - declarative linking
     *  - a Jackson Provider
     *  - the Jackson feature
     *  and sets the application name
     */
    public ApplicationConfig() {
        super();
        this.packages("com.neptune.api.template;com.neptune.api.requestlater;")
                .register(ApplicationConfigHandler.class)
                .register(QueueConfigHandler.class)
                .register(CORSResponseFilter.class)
                // .register(BasicAuthFilter.class)
                .register(DeclarativeLinkingFeature.class)
                .register(JacksonConfigProvider.class)
                .register(JacksonFeature.class)
                .setApplicationName("RequestLater");
    }
}
