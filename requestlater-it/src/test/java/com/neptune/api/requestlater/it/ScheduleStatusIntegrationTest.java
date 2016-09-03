package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleStatusIntegrationTest extends TestCase {

    final static Logger logger = LogManager
            .getLogger(ScheduleStatusIntegrationTest.class);

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    String body;

    String testingElement = "schedules";
    String scheduleId;
    String requestId;
    String responseId;

    public ScheduleStatusIntegrationTest() {
    }

    @Test
    public void test_visibility() throws IOException {

//        request = new Request.Builder()
//                .url(BaseTestConfig.BASE_URL + testingElement).build();
//        response = client.newCall(request).execute();
//        assertEquals("Basic listing of '" + testingElement + "' is not HTTP OK",
//                200, response.code());
//
//        request = new Request.Builder()
//                .url(BaseTestConfig.BASE_URL + testingElement)
//                .post(RequestBody.create(MediaType.parse("application/json"),
//                        "                                                          "
//                                + "{                                               "
//                                + "    \"atTime\": \"2999-12-31T00:00:00.000-0000\""
//                                + "}                                               "))
//                .build();
//        response = client.newCall(request).execute();
//
//        // Store Response Body
//        body = response.body().string();
//        logger.info("Body [visibility]: " + body);
//
//        assertEquals(
//                "Adding element to '" + testingElement
//                        + "' was not possible. Message: " + body,
//                201, response.code());

        // Extract element Id
//        scheduleId = BaseTestConfig.extractId(body);
        scheduleId = "92cd1b18-5aaf-4417-ab69-1b1db6c0af8d";

        request = new Request.Builder().url(
                BaseTestConfig.BASE_URL + testingElement + "/" + scheduleId)
                .build();
        response = client.newCall(request).execute();
        assertEquals(
                "Added element to '" + testingElement + "' is not visible.",
                200, response.code());

        request = new Request.Builder().url(
                BaseTestConfig.BASE_URL + testingElement + "/" + scheduleId)
                .delete().build();
        response = client.newCall(request).execute();
        assertEquals(
                "Element in '" + testingElement + "' is couldn't be deleted.",
                200, response.code());

        request = new Request.Builder().url(
                BaseTestConfig.BASE_URL + testingElement + "/" + scheduleId)
                .build();
        response = client.newCall(request).execute();
        assertEquals(
                "Deleted element in '" + testingElement + "' is still visible.",
                404, response.code());

    }

}
