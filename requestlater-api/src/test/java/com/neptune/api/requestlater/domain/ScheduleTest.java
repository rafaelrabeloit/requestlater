package com.neptune.api.requestlater.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.neptune.api.requestlater.DataExtractorTest;

public class ScheduleTest extends Mockito {

    static final Logger LOGGER = LogManager.getLogger(ScheduleTest.class);

    public static Map<String, List<String>> EXPECTED = new HashMap<>();

    private static final int MARGIN_ERROR = 2 * 1000;
    
    private Instant currentTime;
    private Clock clock;

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
        EXPECTED.put("Date", Arrays.asList("Mon, 20 Mar 2017 20:53:02 GMT"));

        EXPECTED.put("FIRST-DATE", Arrays.asList("01 de janeiro"));

        currentTime = Instant.now();

        // create a mock clock which returns currentTime
        clock = mock(Clock.class);
        when(clock.instant()).thenAnswer(i -> currentTime);
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

    @Test
    public void test_foresee_simple() throws ParseException {

        Schedule schedule = new Schedule();

        schedule.setAt("RRULE:FREQ=MINUTELY;");

        assertEquals(
                Instant.now().plus(1, ChronoUnit.MINUTES)
                        .truncatedTo(ChronoUnit.MINUTES).toString(),
                schedule.getIncomingTime().truncatedTo(ChronoUnit.MINUTES)
                        .toString());

    }

    @Test
    public void test_foresee_simpleWithMultipleFires() throws ParseException {

        Schedule schedule = new Schedule(clock);

        schedule.setAt("RRULE:FREQ=MINUTELY;");

        long diff = currentTime.plus(1, ChronoUnit.MINUTES)
                .until(schedule.getIncomingTime(), ChronoUnit.MILLIS);

        assertTrue("Scheduling time dont match in the 1st occurrence by " + diff
                + "ms", Math.abs(diff) < MARGIN_ERROR);

        assertNull("last time not invalid before first fire",
                schedule.getLastTime());

        // offset by one min
        currentTime = currentTime.plus(1, ChronoUnit.MINUTES);

        // and foresee next
        schedule.nextIncomingTime();

        assertNotNull("LastTime should not be null", schedule.getLastTime());

        diff = currentTime.until(schedule.getLastTime(), ChronoUnit.MILLIS);

        assertTrue("Failed to set last time that fire to now by " + diff + "ms",
                Math.abs(diff) < MARGIN_ERROR);

        diff = currentTime.plus(1, ChronoUnit.MINUTES)
                .until(schedule.getIncomingTime(), ChronoUnit.MILLIS);

        assertTrue("Scheduling time dont match in the 2nd occurrence by " + diff
                + "ms", Math.abs(diff) < MARGIN_ERROR);

        // offset by one min
        currentTime = currentTime.plus(1, ChronoUnit.MINUTES);

        // and foresee next
        schedule.nextIncomingTime();

        diff = currentTime.until(schedule.getLastTime(), ChronoUnit.MILLIS);

        assertTrue("Failed to set last time that fire to now by " + diff + "ms",
                Math.abs(diff) < MARGIN_ERROR);

        diff = currentTime.plus(1, ChronoUnit.MINUTES)
                .until(schedule.getIncomingTime(), ChronoUnit.MILLIS);

        assertTrue("Scheduling time dont match in the 3rd occurrence by " + diff
                + "ms", Math.abs(diff) < MARGIN_ERROR);

    }

    @Test
    public void test_foresee_interval() throws ParseException {

        Schedule schedule = new Schedule();

        schedule.setAt("RRULE:INTERVAL=2;FREQ=MINUTELY;");

        assertEquals(
                Instant.now().plus(2, ChronoUnit.MINUTES)
                        .truncatedTo(ChronoUnit.MINUTES).toString(),
                schedule.getIncomingTime().truncatedTo(ChronoUnit.MINUTES)
                        .toString());

    }

    @Test
    public void test_foresee_intervalBy2WithOcurrence() throws ParseException {

        Schedule schedule = new Schedule(clock);

        schedule.setAt("RRULE:INTERVAL=2;FREQ=MINUTELY;");

        long diff = currentTime.plus(2, ChronoUnit.MINUTES)
                .until(schedule.getIncomingTime(), ChronoUnit.MILLIS);

        assertTrue("Scheduling time dont match in the 1st occurrence by " + diff
                + "ms", Math.abs(diff) < MARGIN_ERROR);

        // offset by two min
        currentTime = currentTime.plus(2, ChronoUnit.MINUTES).plusSeconds(1);

        // and foresee next
        schedule.nextIncomingTime();

        assertNotNull("LastTime should not be null", schedule.getLastTime());

        diff = currentTime.until(schedule.getLastTime(), ChronoUnit.MILLIS);

        assertTrue("Failed to set last time that fire to now by " + diff + "ms",
                Math.abs(diff) < MARGIN_ERROR);

        diff = currentTime.plus(2, ChronoUnit.MINUTES)
                .until(schedule.getIncomingTime(), ChronoUnit.MILLIS);

        assertTrue("Scheduling time dont match in the 2nd occurrence " + diff
                + "ms", Math.abs(diff) < MARGIN_ERROR);
    }

}
