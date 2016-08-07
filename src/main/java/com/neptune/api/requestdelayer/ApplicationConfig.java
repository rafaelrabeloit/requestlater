package com.neptune.api.requestdelayer;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        super();
        this.packages(
                "com.neptune.api.template;com.neptune.api.requestdelayer;")
                .register(DeclarativeLinkingFeature.class)
                .register(ApplicationEventHandler.class)
                .register(CORSResponseFilter.class)
//                .register(BasicAuthFilter.class)
                .setApplicationName("RequestDelayer");
    }
}