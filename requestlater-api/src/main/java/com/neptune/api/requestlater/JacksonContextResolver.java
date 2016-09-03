package com.neptune.api.requestlater;

import java.text.SimpleDateFormat;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    private ObjectMapper objectMapper;

    private String mask = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public JacksonContextResolver() {

        this.objectMapper = new ObjectMapper();

        this.objectMapper.setSerializationInclusion(Include.NON_DEFAULT);

        // JodaModule jodaModule = new JodaModule();
        // this.objectMapper.registerModule(jodaModule);

        this.objectMapper.configure(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.objectMapper.setDateFormat(new SimpleDateFormat(mask));
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return this.objectMapper;
    }

}