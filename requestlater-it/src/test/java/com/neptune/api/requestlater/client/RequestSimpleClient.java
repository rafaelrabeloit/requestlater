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

public class RequestSimpleClient {

    static final Logger LOGGER = LogManager
            .getLogger(RequestSimpleClient.class);

    OkHttpClient client = new OkHttpClient();

    public String testingElement = "requests";

    public RequestSimpleClient() {
    }

    public Response list(String scheduleId) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("schedules").addPathSegment(scheduleId)
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response create(String scheduleId, String target)
            throws IOException {
        return create(scheduleId, target, "", "", "");

    }

    public Response create(String scheduleId, String target, String headers,
            String content, String method) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("schedules").addPathSegment(scheduleId)
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                               "
                                + "{                                    "
                                + (method.length() == 0 ? "" : "\"method\": \"" + method + "\",")
                                + (headers.length() == 0 ? "" : "\"headers\": {" + headers + "},")
                                + (content.length() == 0 ? "" : "\"content\": \"" + content + "\",")
                                + "    \"targetUri\": \"" + target + "\""
                                + "}                                    "))
                .build();
        Response response = client.newCall(request).execute();
        return response;

    }

    public Response get(String scheduleId, String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("schedules").addPathSegment(scheduleId)
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;

    }

    public Response delete(String scheduleId, String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment("schedules").addPathSegment(scheduleId)
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).delete().build();
        Response response = client.newCall(request).execute();
        return response;
    }

}
