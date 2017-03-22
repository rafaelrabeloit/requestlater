package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neptune.api.requestlater.client.RequestSimpleClient;
import com.neptune.api.requestlater.client.ScheduleSimpleClient;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestIntegrationTest extends TestCase {

    static final Logger LOGGER = LogManager
            .getLogger(RequestIntegrationTest.class);

    OkHttpClient httpClient = new OkHttpClient();
    ScheduleSimpleClient scheduleClient = new ScheduleSimpleClient();
    RequestSimpleClient client = new RequestSimpleClient();

    Request request;
    Response response;

    String body;

    String scheduleId;
    String id;

    public RequestIntegrationTest() {
    }

    @Before
    public void setUp() throws IOException {
        response = scheduleClient.create("true",
                "2999-12-31T00:00:00.000-0000");
        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);
    }

    @After
    public void tearDown() throws IOException {
        scheduleClient.delete(scheduleId);
    }

    @Test
    public void test_visibilityStatus() throws IOException {

        request = new Request.Builder().url(BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(client.testingElement).build()).build();
        response = httpClient.newCall(request).execute();
        assertEquals(
                "'" + client.testingElement
                        + "' should NOT be visible without Schedule context",
                404, response.code());

        response = client.list(scheduleId);
        assertEquals("Basic listing of '" + client.testingElement
                + "' is not HTTP OK", 200, response.code());

        response = client.create(scheduleId, "http://localhost/");
        body = response.body().string();
        assertEquals(
                "Adding element to '" + client.testingElement
                        + "' was not possible. Message: " + body,
                201, response.code());

        // Extract element Id
        id = BaseTestConfig.extractId(body);
        response = client.get(scheduleId, id);
        assertEquals("Added element to '" + client.testingElement
                + "' is not visible.", 200, response.code());

        response = client.delete(scheduleId, id);
        assertEquals("Element in '" + client.testingElement
                + "' couldn't be deleted.", 200, response.code());

        response = client.get(scheduleId, id);
        assertEquals("Deleted element in '" + client.testingElement
                + "' is still visible.", 404, response.code());

        response = client.get(scheduleId,
                "00000000-0000-0000-0000-000000000000");
        assertEquals(
                "Arbitrary Id in '" + client.testingElement + "' is visible!.",
                404, response.code());
    }

}
