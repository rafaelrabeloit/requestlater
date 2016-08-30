package com.neptune.api.requestlater.domain;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestTest extends Mockito {

    public RequestTest() {
    }

    @Test
    public void test_buildHttpRequest() {

        Request r = new Request();

        r.setTargetUri("http://localhost/");
        r.setMethod(HttpMethods.GET);
        r.getHeaders().put("Accept", "text/html;q=0.9,image/webp,*/*;q=0.8");
        r.getHeaders().put("Accept-Encoding", "gzip, deflate, sdch, br");
        r.getHeaders().put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.4");

        HttpUriRequest request = r.build();

        assertEquals("Request is not well-formed",
                "GET http://localhost/ HTTP/1.1", request.toString());

    }

}
