package com.neptune.api.requestlater.it;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl.Builder;

/**
 * TODO: READ FROM FILE
 *
 * @author Rafael
 *
 */
public class BaseTestConfig {

    /**
     * Base URL to run the tests against. It can be the IT server, can be the
     * Staging or Development server.
     */
    public static final Builder getBaseUrlBuilder() {
        return new Builder().scheme("http").host("localhost").port(8080)
                .addPathSegment("requestlater-api");
    }

    public static String extractId(String json) {
        String ret = "";

        Pattern pattern = Pattern.compile("\"id\"\\ *:\\ *\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            ret = matcher.group(1);
        } else {
            throw new RuntimeException("Id not found");
        }

        return ret;
    }
}
