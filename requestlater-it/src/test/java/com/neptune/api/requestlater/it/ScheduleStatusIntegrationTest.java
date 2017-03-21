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

public class ScheduleStatusIntegrationTest extends TestCase {

    static final Logger LOGGER = LogManager
            .getLogger(ScheduleStatusIntegrationTest.class);

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    String body;

    HttpUrl baseURL;
    String testingElement = "schedules";
    String scheduleId;

    public ScheduleStatusIntegrationTest() {
    }

    @Test
    public void test_scheduleVisibility() throws IOException {

        baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals("Basic listing of '" + testingElement + "' is not HTTP OK",
                200, response.code());

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
        LOGGER.info("Schedule Body [visibility]: " + body);

        assertEquals(
                "Adding element to '" + testingElement
                        + "' was not possible. Message: " + body,
                201, response.code());

        // Extract element Id
        scheduleId = BaseTestConfig.extractId(body);
        baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).addPathSegment(scheduleId)
                .build();

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

        baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement)
                .addPathSegment("00000000-0000-0000-0000-000000000000").build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals("Arbitrary Id in '" + testingElement + "' is visible!.",
                404, response.code());

    }

}
