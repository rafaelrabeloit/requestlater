package com.neptune.api.requestlater.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.neptune.api.requestlater.client.ScheduleSimpleClient;

import junit.framework.TestCase;
import okhttp3.Response;

public class ScheduleIntegrationTest extends TestCase {

    static final Logger LOGGER = LogManager
            .getLogger(ScheduleIntegrationTest.class);

    ScheduleSimpleClient client = new ScheduleSimpleClient();

    Response response;
    String body;
    String id;

    public ScheduleIntegrationTest() {
    }

    @Test
    public void test_visibilityStatus() throws IOException {

        response = client.list();
        assertEquals("Basic listing of '" + client.testingElement
                + "' is not HTTP OK", 200, response.code());

        response = client.create("true", "2999-12-31T00:00:00.000-0000");
        body = response.body().string();
        assertEquals(
                "Adding element to '" + client.testingElement
                        + "' was not possible. Message: " + body,
                201, response.code());

        // Extract element Id
        id = BaseTestConfig.extractId(body);
        response = client.get(id);
        assertEquals("Added element to '" + client.testingElement
                + "' is not visible.", 200, response.code());

        response = client.delete(id);
        assertEquals("Element in '" + client.testingElement
                + "' couldn't be deleted.", 200, response.code());

        response = client.get(id);
        assertEquals("Deleted element in '" + client.testingElement
                + "' is still visible.", 404, response.code());

        response = client.get("00000000-0000-0000-0000-000000000000");
        assertEquals(
                "Arbitrary Id in '" + client.testingElement + "' is visible!.",
                404, response.code());

    }

}
