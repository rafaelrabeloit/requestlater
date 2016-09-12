package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResponseStatusIntegrationTest extends TestCase {
    final static Logger logger = LogManager
            .getLogger(ResponseStatusIntegrationTest.class);

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    HttpUrl baseURL;
    String testingElement = "responses";
    String testingTarget = BaseTestConfig.getBaseUrlBuilder().build()
            .toString();
    String body;

    String scheduleId;
    String requestId;
    String responseId;

    public ResponseStatusIntegrationTest() {
    }

    @Test
    public void test_responseVisibility()
            throws IOException, InterruptedException {

        long at = DateTime.now().plusSeconds(3).getMillis();

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .build();

        request = new Request.Builder().url(baseURL)
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                                   "
                                + "{                                        "
                                + "    \"atTime\": " + at
                                + "}                                        "))
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
        assertEquals("Basic listing of '" + testingElement + "' is not HTTP OK",
                200, response.code());

        request = new Request.Builder()
                .url(baseURL).post(RequestBody
                        .create(MediaType.parse("application/json"), "{}"))
                .build();
        response = client.newCall(request).execute();
        assertEquals(
                "Creation of '" + testingElement + "' should not be possible",
                405, response.code());

        while (at + 2000 > DateTime.now().getMillis()) {
            Thread.sleep(100);
        }

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals(
                "Basic listing of '" + testingElement
                        + "' is not HTTP OK after inserction",
                200, response.code());

        // Store Responses Body
        body = response.body().string();

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));

        // Extract element Id
        responseId = BaseTestConfig.extractId(body);

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("requests")
                .addPathSegment(requestId).addPathSegment("responses")
                .addPathSegment(responseId).build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        assertEquals(
                "Added element to '" + testingElement + "' is not visible.",
                200, response.code());

        assertTrue("Added element to '" + testingElement + "' is not in body.",
                !body.equals(""));

        request = new Request.Builder()
                .url(baseURL).put(RequestBody
                        .create(MediaType.parse("application/json"), "{}"))
                .build();
        response = client.newCall(request).execute();
        assertEquals("'" + testingElement + "' element can not be edited", 405,
                response.code());

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

        // Remove schedule TODO: Will it remove in cascade?
        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("schedules")
                .addPathSegment(scheduleId).build();
        request = new Request.Builder().url(baseURL).delete().build();
        response = client.newCall(request).execute();
    }

}
