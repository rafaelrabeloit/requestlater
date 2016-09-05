package com.neptune.api.requestlater;

import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Provider
public class JacksonConfigProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defaultObjectMapper;

    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper map = new ObjectMapper();

        map.setSerializationInclusion(Include.NON_DEFAULT);
        map.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        map.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        //TODO: THIS SEEMS TO BE MISSING WHEN CUSTOM CONFIG IS ADDED
        map.registerModule(new JaxbAnnotationModule());

        return map;
    }

    public JacksonConfigProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defaultObjectMapper;

    }
}