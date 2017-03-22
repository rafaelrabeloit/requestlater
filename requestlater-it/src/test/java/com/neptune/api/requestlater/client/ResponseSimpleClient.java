package com.neptune.api.requestlater.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neptune.api.requestlater.it.BaseTestConfig;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResponseSimpleClient {

    static final Logger LOGGER = LogManager
            .getLogger(ResponseSimpleClient.class);

    OkHttpClient client = new OkHttpClient();

    public String testingElement = "responses";

    public ResponseSimpleClient() {
    }

    public Response list(String requestId) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("requests").addPathSegment(requestId)
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response create(String requestId) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("requests").addPathSegment(requestId)
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder()
                .url(baseURL).post(RequestBody
                        .create(MediaType.parse("application/json"), "{}"))
                .build();
        Response response = client.newCall(request).execute();
        return response;

    }

    public Response get(String requestId, String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("requests").addPathSegment(requestId)
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;

    }

    public Response delete(String requestId, String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("requests").addPathSegment(requestId)
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).delete().build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response edit(String requestId, String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("requests").addPathSegment(requestId)
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder()
                .url(baseURL).put(RequestBody
                        .create(MediaType.parse("application/json"), "{}"))
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
