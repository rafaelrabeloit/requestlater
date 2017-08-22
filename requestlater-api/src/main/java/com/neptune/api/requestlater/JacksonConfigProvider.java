package com.neptune.api.requestlater;

import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.neptune.api.requestlater.domain.Schedule;

/**
 * Overrides Jackson provider, for how to serialize dates
 * 
 * @author Rafael R. Itajuba
 *
 */
@Provider
public class JacksonConfigProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defaultObjectMapper;

    /**
     * Overrides Jackson provider, for how to serialize dates
     * @return the object mapper
     */
    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper map = new ObjectMapper();

        map.setSerializationInclusion(Include.NON_DEFAULT);
        map.setDateFormat(new SimpleDateFormat(Schedule.DATE_FORMAT));
        //TODO: THIS SEEMS TO BE MISSING WHEN CUSTOM CONFIG IS ADDED
        map.registerModule(new JaxbAnnotationModule());
        map.registerModule(new JSR310Module());
        map.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return map;
    }

    public JacksonConfigProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return defaultObjectMapper;

    }
}
