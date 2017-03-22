package com.neptune.api.requestlater.client;

import java.io.IOException;

import com.neptune.api.requestlater.it.BaseTestConfig;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleSimpleClient {

    OkHttpClient client = new OkHttpClient();

    public String testingElement = "schedules";

    public ScheduleSimpleClient() {
    }

    public Response list() throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response create(String active, String at) throws IOException {
        return createBase(active, "\"" + at + "\"");
    }

    public Response create(String active, long at) throws IOException {
        return createBase(active, String.valueOf(at));
    }

    private Response createBase(String active, String at) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).build();

        Request request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                         "
                                + "{                              "
                                + "    \"atTime\": " + at + ","
                                + "    \"active\": " + active + " "
                                + "}                              "))
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response get(String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response delete(String id) throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL).delete().build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response edit(String id, String active, String at)
            throws IOException {
        return editBase(id, active, "\"" + at + "\"");
    }

    public Response edit(String id, String active, long at) throws IOException {
        return editBase(id, active, String.valueOf(at));
    }

    private Response editBase(String id, String active, String at)
            throws IOException {
        HttpUrl baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).addPathSegment(id).build();

        Request request = new Request.Builder().url(baseURL)
                .put(RequestBody.create(MediaType.parse("application/json"),
                        "                                         "
                                + "{                              "
                                + "    \"atTime\": " + at + ","
                                + "    \"active\": " + active + " "
                                + "}                              "))
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
