package com.neptune.api.requestlater.it;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl.Builder;

/**
 * @author Rafael
 *
 */
public class BaseTestConfig {

    static String HOSTNAME = "localhost";
    static int HOSTPORT = 8080;

    static {
        String hostname = System.getenv("requestlater_it_hostname");
        String hostport = System.getenv("requestlater_it_hostport");
        if (hostname != null) {
            HOSTNAME = hostname;
        }
        if (hostport != null) {
            HOSTPORT = Integer.parseInt(hostport);
        }
    }

    static int TARGET_HOSTPORT = 1080;
    static String TARGET_HOSTNAME = "127.0.0.1";
    static String TARGET = "http://" + TARGET_HOSTNAME + ":" + TARGET_HOSTPORT;

    static int TIME_TO_TARGET_RESPOND = 1000; //ms
    
    /**
     * Base URL to run the tests against. It can be the IT server, can be the
     * Staging or Development server.
     */
    public static final Builder getBaseUrlBuilder() {
        return new Builder().scheme("http").host(HOSTNAME).port(HOSTPORT)
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
