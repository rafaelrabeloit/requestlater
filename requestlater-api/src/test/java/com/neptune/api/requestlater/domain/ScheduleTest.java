package com.neptune.api.requestlater.domain;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.neptune.api.requestlater.DataExtractorTest;

public class ScheduleTest extends Mockito {

    public static Map<String, List<String>> EXPECTED = new HashMap<>();

    public ScheduleTest() {
    }

    @Before
    public void setUp() {
        EXPECTED.put("Cache-Control", Arrays.asList("private"));
        EXPECTED.put("Content-Type", Arrays.asList("text/html"));
        EXPECTED.put("Content-Encoding", Arrays.asList("gzip"));
        EXPECTED.put("Vary", Arrays.asList("Accept-Encoding"));
        EXPECTED.put("Server", Arrays.asList("Microsoft-IIS/8.5"));
        EXPECTED.put("Set-Cookie", Arrays.asList(
                "ASPSESSIONIDQQBRBRQQ=OBFMDAPDCOILJABKPEHNPLJD; path=/"));
        EXPECTED.put("X-Powered-By", Arrays.asList("ASP.NET"));
        EXPECTED.put("Date",
                Arrays.asList("Mon, 20 Mar 2017 20:53:02 GMT"));

        EXPECTED.put("FIRST-DATE", Arrays.asList("01 de janeiro"));
    }

    @Test
    public void test_extractVariables() {

        Schedule schedule = new Schedule();
        Response response = new Response();
        Request context = new Request();

        response.getHeaders().put("Cache-Control", "private");
        response.getHeaders().put("Content-Type", "text/html");
        response.getHeaders().put("Content-Encoding", "gzip");
        response.getHeaders().put("Vary", "Accept-Encoding");
        response.getHeaders().put("Server", "Microsoft-IIS/8.5");
        response.getHeaders().put("Set-Cookie",
                "ASPSESSIONIDQQBRBRQQ=OBFMDAPDCOILJABKPEHNPLJD; path=/");
        response.getHeaders().put("X-Powered-By", "ASP.NET");
        response.getHeaders().put("Date", "Mon, 20 Mar 2017 20:53:02 GMT");

        response.setContent(DataExtractorTest.CASE1);
        context.getExtractors().put("FIRST-DATE",
                "tr:first-child > td:nth-child(2)");

        schedule.addVariables(response, context);

        EXPECTED.forEach((key, val) -> {
            assertEquals("Variable not present", true,
                    schedule.getVariables().containsKey(key));

            assertEquals("Variable " + key + " not extracted correctly",
                    val.toString(),
                    schedule.getVariables().get(key).toString());
        });

    }

}
