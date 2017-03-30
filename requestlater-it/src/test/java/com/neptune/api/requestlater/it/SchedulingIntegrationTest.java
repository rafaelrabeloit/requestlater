package com.neptune.api.requestlater.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
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
    public void prepare(String active, String at)
            throws IOException, InterruptedException {

        response = scheduleClient.create(active, at);

        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, BaseTestConfig.TARGET);

        body = response.body().string();
        requestId = BaseTestConfig.extractId(body);

        response = responseClient.list(requestId);
        body = response.body().string();

        assertEquals("Responses are not empty!", "[]", body);
    }

    public void waitResponses(DateTime until)
            throws InterruptedException, IOException {

        while (until.isAfterNow()) {
            Thread.sleep(100);
        }

        response = responseClient.list(requestId);
        body = response.body().string();
    }

    @Test
    public void test_checkDelayedResponse()
            throws IOException, InterruptedException {

        DateTime at = DateTime.now().plusSeconds(3);

        prepare("true", at.toString());

        waitResponses(at.plusSeconds(1));

        assertNotEquals(
                "Delayed Request didn't work because didn't generate Responses",
                "[]", body);

    }

    @Test
    public void test_checkDelayedResponseWithRecurrenceRule()
            throws IOException, InterruptedException {

        DateTime at = DateTime.now().plusSeconds(3);

        // firing every 3 seconds
        prepare("true", "RRULE:FREQ=SECONDLY;INTERVAL=3;COUNT=2");

        waitResponses(at.plusSeconds(2));

        assertNotEquals(
                "The 1st delayed request didn't work because didn't generate "
                        + "Responses",
                "[]", body);

        assertEquals("There was an incorrect number of responses", 1,
                StringUtils.countMatches(body, "id"));

        waitResponses(at.plusSeconds(2 + (3 + 2)));

        assertNotEquals(
                "The 2nd delayed request didn't work because didn't generate "
                        + "Responses",
                "[]", body);

        assertEquals("There was an incorrect number of responses", 2,
                StringUtils.countMatches(body, "id"));
    }

    @Test
    public void test_disablingSchedule()
            throws IOException, InterruptedException {

        DateTime at = DateTime.now().plusSeconds(3);

        prepare("false", at.toString());

        waitResponses(at.plusSeconds(1));

        assertEquals("Delayed Request didn't work because there are Responses",
                "[]", body);

    }
}
