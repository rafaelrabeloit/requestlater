package com.neptune.api.requestlater.domain;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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

        HttpUriRequest request = r.extract();

        assertEquals("Request is not well-formed",
                "GET http://localhost/ HTTP/1.1", request.toString());

    }

    @Test
    public void test_buildHttpRequestWithVariables()
            throws UnsupportedOperationException, IOException {

        Schedule schedule = new Schedule();
        Request request = new Request();

        request.setSchedule(schedule);
        request.setTargetUri("http://localhost/");
        request.setMethod(HttpMethods.POST);
        request.getHeaders().put("Accept", "Content-Type");
        request.getHeaders().put("Date", "FIRST-DATE");
        request.setContent("bla bla bla Set-Cookie bla bla bla INFO bla bla bla");

        schedule.getVariables().put("Cache-Control", Arrays.asList("private"));
        schedule.getVariables().put("Content-Type", Arrays.asList("text/html"));
        schedule.getVariables().put("Content-Encoding", Arrays.asList("gzip"));
        schedule.getVariables().put("Vary", Arrays.asList("Accept-Encoding"));
        schedule.getVariables().put("Server",
                Arrays.asList("Microsoft-IIS/8.5"));
        schedule.getVariables().put("Set-Cookie", Arrays.asList(
                "ASPSESSIONIDQQBRBRQQ=OBFMDAPDCOILJABKPEHNPLJD; path=/"));
        schedule.getVariables().put("X-Powered-By", Arrays.asList("ASP.NET"));
        schedule.getVariables().put("Date",
                Arrays.asList("Mon, 20 Mar 2017 20:53:02 GMT"));

        schedule.getVariables().put("FIRST-DATE",
                Arrays.asList("01 de janeiro"));

        schedule.getVariables().put("INFO",
                Arrays.asList("Meu valor de variavel"));

        HttpUriRequest request_result = request.extract();

        assertEquals("Header not reconized as variable for another header",
                schedule.getVariables().get("Content-Type").get(0),
                request_result.getHeaders("Accept")[0].getValue());

        assertEquals("Extracted Data not reconized as variable for header",
                schedule.getVariables().get("FIRST-DATE").get(0),
                request_result.getHeaders("Date")[0].getValue());

        InputStream in = ((HttpEntityEnclosingRequestBase) request_result)
                .getEntity().getContent();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String read;

        while ((read = br.readLine()) != null)
            sb.append(read);

        br.close();
        assertEquals("Header not reconized as variable for content",
                "bla bla bla"
                + " ASPSESSIONIDQQBRBRQQ=OBFMDAPDCOILJABKPEHNPLJD; path=/"
                + " bla bla bla"
                + " Meu valor de variavel"
                + " bla bla bla",
                sb.toString());

    }
}
