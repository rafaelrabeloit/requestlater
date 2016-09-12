package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SchedulingIntegrationTest extends TestCase {

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    String body;

    HttpUrl baseURL;
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

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .build();

        request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                           "
                                + "{                                "
                                + "    \"atTime\": " + at
                                + "}                                "))
                .build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        // Extract element Id
        scheduleId = BaseTestConfig.extractId(body);

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).addPathSegment("requests").build();

        request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                                      "
                                + "{                                           "
                                + "    \"targetUri\": \"" + testingTarget + "\""
                                + "}                                           "))
                .build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        // Extract element Id
        requestId = BaseTestConfig.extractId(body);

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("requests")
                .addPathSegment(requestId).addPathSegment("responses").build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        assertTrue("Responses are not empty!", body.equals("[]"));

        while (at + 2000 > DateTime.now().getMillis()) {
            Thread.sleep(100);
        }

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));
        
        // Remove schedule TODO: Will it remove in cascade?
        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).build();
        request = new Request.Builder().url(baseURL).delete().build();
        response = client.newCall(request).execute();
        
    }
}
