package com.neptune.api.requestlater;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataExtractor {

    final static Logger logger = LogManager.getLogger(DataExtractor.class);

    public static Map<String, List<String>> extractWithRegex(String content,
            Map<String, String> rules) {

        // result object for <variable, values>
        Map<String, List<String>> result = new HashMap<>();

        // for every rule, get its variable identifier 'v'
        rules.forEach((v, r) -> {

            // matcher object.
            Matcher matcher = Pattern.compile(r).matcher(content);

            // matching array for a rule
            List<String> matches = new LinkedList<>();

            while (matcher.find()) {
                matches.add(matcher.group(1));
            }

            result.put(v, matches);
        });

        return result;
    }

    public static Map<String, List<String>> extractWithSelector(String content,
            Map<String, String> rules) {

        // result object for <variable, values>
        Map<String, List<String>> result = new HashMap<>();

        // ...
        Document document = Jsoup.parse(content);

        // for every rule, get its variable identifier 'v'
        rules.forEach((v, r) -> {

            Elements els = document.select(rules.get(v));

            List<String> matches = new LinkedList<>();

            for (Element el : els) {
                matches.add(el.ownText());
            }

            result.put(v, matches);
        });

        return result;
    }

}
