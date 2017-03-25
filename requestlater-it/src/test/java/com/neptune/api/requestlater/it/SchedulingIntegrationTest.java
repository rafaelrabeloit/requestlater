package com.neptune.api.requestlater.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

    long at = 0;

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
    public void prepare(String active, String recurrence)
            throws IOException, InterruptedException {

        // schedule request for 3sec from now
        at = DateTime.now().plusSeconds(3).getMillis();

        response = scheduleClient.create(active, at, recurrence);

        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, BaseTestConfig.TARGET);

        body = response.body().string();
        requestId = BaseTestConfig.extractId(body);

        response = responseClient.list(requestId);
        body = response.body().string();

        assertEquals("Responses are not empty!", "[]", body);
    }

    public void waitResponses(int time)
            throws InterruptedException, IOException {

        while (at + time > DateTime.now().getMillis()) {
            Thread.sleep(100);
        }

        response = responseClient.list(requestId);
        body = response.body().string();
    }

    @Test
    public void test_checkDelayedResponse()
            throws IOException, InterruptedException {

        prepare("true", "");

        waitResponses(2000);

        assertNotEquals(
                "Delayed Request didn't work because didn't generate Responses",
                "[]", body);

    }

//    @Test
//    public void test_checkDelayedResponseWithRecurrenceRule()
//            throws IOException, InterruptedException {
//
//        // firing every 3 seconds
//        prepare("true", "RRULE:FREQ=SECONDLY;INTERVAL=3;COUNT=2");
//
//        waitResponses(2000);
//
//        assertNotEquals(
//                "The 1st delayed request didn't work because didn't generate "
//                        + "Responses",
//                "[]", body);
//
//        waitResponses(3000);
//
//        assertEquals(
//                "The 2nd delayed request didn't work because didn't generate "
//                        + "Responses",
//                "[]", body);
//
//    }

    @Test
    public void test_disablingSchedule()
            throws IOException, InterruptedException {

        prepare("false", "");

        waitResponses(2000);

        assertEquals("Delayed Request didn't work because there are Responses",
                "[]", body);

    }
}
