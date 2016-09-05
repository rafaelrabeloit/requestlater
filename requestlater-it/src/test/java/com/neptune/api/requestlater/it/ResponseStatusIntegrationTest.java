package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResponseStatusIntegrationTest extends TestCase {
    final static Logger logger = LogManager
            .getLogger(ResponseStatusIntegrationTest.class);

    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    HttpUrl baseURL;
    String testingElement = "requests";
    String body;

    String scheduleId;
    String requestId;
    String responseId;

    public ResponseStatusIntegrationTest() {
    }

    @Test
    public void test_responseVisibility() throws IOException {

        baseURL = BaseTestConfig.getBaseUrlBuilder()
                .addPathSegment(testingElement).build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();
        assertEquals(
                "'" + testingElement
                        + "' should NOT be visible without Requests context",
                404, response.code());

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("requests")
                .build();

        // ...
    }

}
