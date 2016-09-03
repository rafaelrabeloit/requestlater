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

public class RequestStatusIntegrationTest extends TestCase {
    
    final static Logger logger = LogManager
            .getLogger(RequestStatusIntegrationTest.class);
    
    OkHttpClient client = new OkHttpClient();

    Request request;
    Response response;

    String body;

    String scheduleId;
    String requestId;
    String responseId;
    
    public RequestStatusIntegrationTest() {
    }

    @Test
    public void test_visibility() throws IOException {
        // //
        // request = new Request.Builder()
        // .url(BaseTestConfig.BASE_URL + "schedules").build();
        // response = client.newCall(request).execute();
        // assertEquals("Schedules not visible", 200, response.code());
        //
        // request = new Request.Builder()
        // .url(BaseTestConfig.BASE_URL + "responses").build();
        // response = client.newCall(request).execute();
        // assertEquals(
        // "'Responses' should NOT be visible without Request context",
        // 404, response.code());
        //
        // //
        // request = new Request.Builder()
        // .url(BaseTestConfig.BASE_URL + "schedules")
        // .post(RequestBody.create(MediaType.parse("application/json"),
        // " "
        // + "{ "
        // + " \"atTime\": \"2999-12-31T00:00:00.000-00:00\""
        // + "} "))
        // .build();
        // response = client.newCall(request).execute();
        // body = response.body().string();
        // logger.info("Received from server: " + body);
        // assertEquals("Schedules could not be created. Message: " + body, 201,
        // response.code());
        //
        // scheduleId = BaseTestConfig.extractId(body);
        // request = new Request.Builder()
        // .url(BaseTestConfig.BASE_URL + "schedules/" + scheduleId)
        // .build();
        // response = client.newCall(request).execute();
        // assertEquals("Schedules could not be retrieved", 200,
        // response.code());
        //
        // request = new Request.Builder()
        // .url(BaseTestConfig.BASE_URL + "schedules/" + scheduleId)
        // .delete().build();
        // response = client.newCall(request).execute();
        // assertEquals("Schedules could not be deleted", 200, response.code());


    }

    @Test
    public void test_basicRequestVisibility() throws IOException {

         request = new Request.Builder()
         .url(BaseTestConfig.BASE_URL + "requests").build();
         response = client.newCall(request).execute();
         assertEquals(
         "'Requests' should NOT be visible without Schedule context",
         404, response.code());


        //
        request = new Request.Builder()
                .url(BaseTestConfig.BASE_URL + "schedules")
                .post(RequestBody.create(MediaType.parse("application/json"),
                        "                                                          "
                                + "{                                               "
                                + "    \"atTime\": \"2999-12-31T00:00:00.000-0000\""
                                + "}                                               "))
                .build();
        response = client.newCall(request).execute();
        body = response.body().string();
        logger.info("Received from server: " + body);

        scheduleId = BaseTestConfig.extractId(body);
        request = new Request.Builder()
                .url(BaseTestConfig.BASE_URL + "schedules/" + scheduleId
                        + "/requests")
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
    }

}
