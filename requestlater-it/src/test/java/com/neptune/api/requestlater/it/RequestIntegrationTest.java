package com.neptune.api.requestlater.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import com.neptune.api.requestlater.client.RequestSimpleClient;
import com.neptune.api.requestlater.client.ResponseSimpleClient;
import com.neptune.api.requestlater.client.ScheduleSimpleClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestIntegrationTest {

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

    @BeforeClass
    public static void configure() {
        new MockServerClient(BaseTestConfig.TARGET_HOSTNAME,
                BaseTestConfig.TARGET_HOSTPORT)
                        .when(request().withMethod("POST").withPath("/"))
                        .respond(response().withStatusCode(200));
    }

    @Before
    public void setUp() throws IOException {
        response = scheduleClient.create("false", 0);
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

        response = client.create(scheduleId, BaseTestConfig.TARGET);
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

    @Test
    public void test_post() throws IOException, InterruptedException {
        ResponseSimpleClient responseClient = new ResponseSimpleClient();

        response = client.create(scheduleId, BaseTestConfig.TARGET,
                "\"Content-Type\": \"application/json\"", "{}", "POST");
        body = response.body().string();
        id = BaseTestConfig.extractId(body);

        response = scheduleClient.edit(scheduleId, "true", 0);
        Thread.sleep(BaseTestConfig.TIME_TO_TARGET_RESPOND);

        response = responseClient.list(id);
        assertEquals(
                "Basic listing of '" + responseClient.testingElement
                        + "' is not HTTP OK after inserction",
                200, response.code());

        // Store Responses Body
        body = response.body().string();

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));

    }

}
