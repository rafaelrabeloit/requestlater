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

import okhttp3.Response;

public class ResponseIntegrationTest {

    static final Logger LOGGER = LogManager
            .getLogger(ResponseIntegrationTest.class);

    ScheduleSimpleClient scheduleClient = new ScheduleSimpleClient();
    RequestSimpleClient requestClient = new RequestSimpleClient();
    ResponseSimpleClient client = new ResponseSimpleClient();

    Response response;

    String body;

    String scheduleId;
    String requestId;
    String id;

    public ResponseIntegrationTest() {
    }

    @BeforeClass
    public static void configure() {
        new MockServerClient(BaseTestConfig.TARGET_HOSTNAME,
                BaseTestConfig.TARGET_HOSTPORT)
                        .when(request().withMethod("GET").withPath("/"))
                        .respond(response().withStatusCode(200));
    }

    @Before
    public void setUp() throws IOException {
        response = scheduleClient.create("false", 0);
        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, BaseTestConfig.TARGET);
        body = response.body().string();
        requestId = BaseTestConfig.extractId(body);

        response = scheduleClient.edit(scheduleId, "true", 0);
    }

    @After
    public void tearDown() throws IOException {
        // Request removed in cascade
        scheduleClient.delete(scheduleId);
    }

    @Test
    public void test_visibilityStatus()
            throws IOException, InterruptedException {

        Thread.sleep(BaseTestConfig.TIME_TO_TARGET_RESPOND);

        response = client.list(requestId);
        assertEquals("Basic listing of '" + client.testingElement
                + "' is not HTTP OK", 200, response.code());

        response = client.create(requestId);
        assertEquals("Creation of '" + client.testingElement
                + "' should not be possible", 405, response.code());

        response = client.list(requestId);
        assertEquals(
                "Basic listing of '" + client.testingElement
                        + "' is not HTTP OK after inserction",
                200, response.code());

        // Store Responses Body
        body = response.body().string();

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));

        // Extract element Id
        id = BaseTestConfig.extractId(body);

        response = client.get(requestId, id);
        assertEquals("Added element to '" + client.testingElement
                + "' is not visible.", 200, response.code());

        assertTrue("Added element to '" + client.testingElement
                + "' is not in body.", !body.equals(""));

        response = client.edit(requestId, id);
        assertEquals(
                "'" + client.testingElement + "' element can not be edited",
                405, response.code());

        response = client.delete(requestId, id);
        assertEquals("Element in '" + client.testingElement
                + "' couldn't be deleted.", 200, response.code());

        response = client.get(requestId, id);
        assertEquals("Deleted element in '" + client.testingElement
                + "' is still visible.", 404, response.code());

        response = client.get(requestId,
                "00000000-0000-0000-0000-000000000000");
        assertEquals(
                "Arbitrary Id in '" + client.testingElement + "' is visible!.",
                404, response.code());
    }

}
