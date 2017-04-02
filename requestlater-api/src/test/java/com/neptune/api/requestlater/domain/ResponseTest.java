package com.neptune.api.requestlater.domain;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.mockito.Mockito;

public class ResponseTest extends Mockito {

    public class HeaderImpl implements Header {

        private String name;
        private String value;

        public HeaderImpl(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public HeaderElement[] getElements() throws ParseException {
            return null;
        }

    }

    public ResponseTest() {
    }

    @Test
    public void test_peelHttpResponse() {
        String body = "Hello Test!";
        Header headers[] = new Header[] {
                new HeaderImpl("Content-Type", "text/html"),
                new HeaderImpl("Server", "Apache"),
                new HeaderImpl("Content-Length", "11") };
        HttpResponse response = mock(HttpResponse.class);
        StatusLine status = mock(StatusLine.class);

        when(status.getStatusCode()).thenReturn(200);
        when(status.getReasonPhrase()).thenReturn("OK");
        when(status.getProtocolVersion())
                .thenReturn(new ProtocolVersion("HTTP", 1, 1));
        when(response.getStatusLine()).thenReturn(status);
        when(response.getLocale()).thenReturn(Locale.US);
        when(response.getAllHeaders()).thenReturn(headers);
        when(response.getEntity()).thenReturn(new StringEntity(body,
                ContentType.create("text/plain", "UTF-8")));

        Response r = new Response();
        r.feed(response);

        assertEquals("Failed to retrieve status", 200,
                r.getStatusCode().intValue());
        assertEquals("Failed to retrieve status", "OK", r.getReasonPhrase());
        assertEquals("Failed to retrieve locale", Locale.US.toString(),
                r.getLocale());
        assertEquals("Failed to retrieve protocol version", "HTTP/1.1",
                r.getProtocolVersion());
        assertEquals("Failed to retrieve all headers", headers.length,
                r.getHeaders().size());
        Arrays.asList(headers).forEach((header) -> {
            assertEquals("Header values don't match", header.getValue(),
                    r.getHeaders().get(header.getName()));
        });
        assertEquals("Failed to retrieve response body", body, r.getContent());

    }

}
