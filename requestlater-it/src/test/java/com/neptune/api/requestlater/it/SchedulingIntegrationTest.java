package com.neptune.api.requestlater.it;

import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import com.neptune.api.requestlater.client.RequestSimpleClient;
import com.neptune.api.requestlater.client.ResponseSimpleClient;
import com.neptune.api.requestlater.client.ScheduleSimpleClient;

import okhttp3.Response;

public class SchedulingIntegrationTest {

    ScheduleSimpleClient scheduleClient = new ScheduleSimpleClient();
    RequestSimpleClient requestClient = new RequestSimpleClient();
    ResponseSimpleClient responseClient = new ResponseSimpleClient();

    Response response;

    String body;

    String scheduleId;
    String requestId;

    public SchedulingIntegrationTest() {
    }

    @BeforeClass
    public static void configure() {
        new MockServerClient(BaseTestConfig.TARGET_HOSTNAME,
                BaseTestConfig.TARGET_HOSTPORT)
                        .when(request().withMethod("GET").withPath("/"))
                        .respond(response().withStatusCode(200));
    }

    @After
    public void tearDown() throws IOException {
        // Request removed in cascade
        scheduleClient.delete(scheduleId);
    }

    /**
     * Prepare the scheduling test
     * 
     * @param active
     *            if the schedule must start active or not
     * @throws IOException
     * @throws InterruptedException
     */
    public void prepare(String active)
            throws IOException, InterruptedException {

        // schedule request for 3sec from now
        long at = DateTime.now().plusSeconds(3).getMillis();

        response = scheduleClient.create(active, at);

        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, BaseTestConfig.TARGET);

        body = response.body().string();
        requestId = BaseTestConfig.extractId(body);

        response = responseClient.list(requestId);
        body = response.body().string();

        assertTrue("Responses are not empty!", body.equals("[]"));

        while (at + 2000 > DateTime.now().getMillis()) {
            Thread.sleep(100);
        }

        response = responseClient.list(requestId);
        body = response.body().string();

    }

    @Test
    public void test_checkDelayedResponse()
            throws IOException, InterruptedException {

        prepare("true");

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));

    }

    @Test
    public void test_disablingSchedule()
            throws IOException, InterruptedException {

        prepare("false");

        assertTrue("Delayed Request didn't work because there are Responses",
                body.equals("[]"));

    }
}
