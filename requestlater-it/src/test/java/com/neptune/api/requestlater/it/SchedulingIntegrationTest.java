package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SchedulingIntegrationTest extends TestCase {

    public SchedulingIntegrationTest() {
    }

    @Test
    public void test_buildHttpRequest() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:9080/requestlater-api/schedules")
                .build();

        Response response = client.newCall(request).execute();
        assertEquals("[]", response.body().string());
//        assertTrue(true);
    }
}
