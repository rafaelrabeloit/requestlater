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
                        "                                                          "
                                + "{                                               "
                                + "    \"atTime\": " + at
                                + "}                                               "))
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
                        "                                                                                                                                                   "
                                + "{                                                                                                                                        "
                                + "    \"targetUri\": \"https://wwwss.shopinvest.com.br/infofundos/fundos/ConteudoTabelaRentabilidade.do?cdSgmtoProdt=1\",                  "
                                + "    \"method\": \"GET\",                                                                                                                 "
                                + "    \"headers\": {                                                                                                                       "
                                + "        \"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\",                                      "
                                + "        \"Accept-Encoding\": \"gzip, deflate, sdch, br\",                                                                                "
                                + "        \"Accept-Language\": \"pt-BR,pt;q=0.8,en-US;q=0.6,en;q=0.4\",                                                                    "
                                + "        \"Cache-Control\": \"max-age=0\",                                                                                                "
                                + "        \"Referer\": \"https://wwwss.shopinvest.com.br/infofundos/fundos/TabelaRentabilidade.do?cdSgmtoProdt=1\",                        "
                                + "        \"Upgrade-Insecure-Requests\": \"1\",                                                                                            "
                                + "        \"User-Agent\": \"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36\""
                                + "    }                                                                                                                                    "
                                + "}                                                                                                                                        "))
                .build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        // Extract element Id
        requestId = BaseTestConfig.extractId(body);

        while (at + 2000 > DateTime.now().getMillis()) {
            Thread.sleep(100);
        }

        baseURL = BaseTestConfig.getBaseUrlBuilder().addPathSegment("requests")
                .addPathSegment(requestId).addPathSegment("responses").build();

        request = new Request.Builder().url(baseURL).build();
        response = client.newCall(request).execute();

        // Store Response Body
        body = response.body().string();

        assertTrue(
                "Delayed Request didn't work because didn't generate Responses",
                !body.equals("[]"));
    }
}
