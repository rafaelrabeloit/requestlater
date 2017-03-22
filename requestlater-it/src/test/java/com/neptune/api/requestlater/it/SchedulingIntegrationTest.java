package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

import com.neptune.api.requestlater.client.RequestSimpleClient;
import com.neptune.api.requestlater.client.ResponseSimpleClient;
import com.neptune.api.requestlater.client.ScheduleSimpleClient;

import junit.framework.TestCase;
import okhttp3.Response;

public class SchedulingIntegrationTest extends TestCase {

    ScheduleSimpleClient scheduleClient = new ScheduleSimpleClient();
    RequestSimpleClient requestClient = new RequestSimpleClient();
    ResponseSimpleClient responseClient = new ResponseSimpleClient();
    
    Response response;

    String body;

    String testingTarget = BaseTestConfig.getBaseUrlBuilder().build()
            .toString();

    String scheduleId;
    String requestId;

    public SchedulingIntegrationTest() {
    }

    @Test
    public void test_checkDelayedResponse()
            throws IOException, InterruptedException {

        long at = DateTime.now().plusSeconds(3).getMillis();

        response = scheduleClient.create("true", at);

        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, testingTarget);

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

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));

        scheduleClient.delete(scheduleId);
    }

    @Test
    public void test_disablingSchedule()
            throws IOException, InterruptedException {

        long at = DateTime.now().plusSeconds(3).getMillis();

        response = scheduleClient.create("true", at);

        body = response.body().string();
        scheduleId = BaseTestConfig.extractId(body);

        response = requestClient.create(scheduleId, testingTarget);

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

        assertTrue(
                "Delayed Request didn't work because there are Responses",
                !body.equals("[]"));

        scheduleClient.delete(scheduleId);
    }
}
