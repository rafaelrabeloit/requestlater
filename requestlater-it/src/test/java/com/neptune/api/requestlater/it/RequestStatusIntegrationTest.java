package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestStatusIntegrationTest extends TestCase {

    final static Logger logger = LogManager
            .getLogger(RequestStatusIntegrationTest.class);

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    String body;

    HttpUrl baseURL;
    String testingElement = "requests";

    String scheduleId;
    String requestId;

    public RequestStatusIntegrationTest() {
    }

    @Test
    public void test_requestVisibility() throws IOException {

        baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals(
                "'" + testingElement
                        + "' should NOT be visible without Schedule context",
                404, response.code());

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .build();

        request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                                          "
                                + "{                                               "
                                + "    \"atTime\": \"2999-12-31T00:00:00.000-0000\""
                                + "}                                               "))
                .build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();
        logger.info("Schedule Body [visibility]: " + body);

        scheduleId = BaseTestConfig.extractId(body);
        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).addPathSegment(testingElement)
                .build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals("Basic listing of '" + testingElement + "' is not HTTP OK",
                200, response.code());

        request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                                  "
                                + "{                                       "
                                + "    \"targetUri\": \"http://localhost/\""
                                + "}                                       "))
                .build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();
        logger.info("Request Body [visibility]: " + body);

        assertEquals(
                "Adding element to '" + testingElement
                        + "' was not possible. Message: " + body,
                201, response.code());

        // Extract element Id
        requestId = BaseTestConfig.extractId(body);
        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).addPathSegment(testingElement)
                .addPathSegment(requestId).build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals(
                "Added element to '" + testingElement + "' is not visible.",
                200, response.code());

        request = new Request.Builder().url(baseURL).delete().build();
        response = client.newCall(request).execute();
        assertEquals("Element in '" + testingElement + "' couldn't be deleted.",
                200, response.code());

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals(
                "Deleted element in '" + testingElement + "' is still visible.",
                404, response.code());

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).addPathSegment(testingElement)
                .addPathSegment("00000000-0000-0000-0000-000000000000").build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals("Arbitrary Id in '" + testingElement + "' is visible!.",
                404, response.code());

        // Remove schedule
        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).build();
        request = new Request.Builder().url(baseURL).delete().build();
        response = client.newCall(request).execute();
    }

}
