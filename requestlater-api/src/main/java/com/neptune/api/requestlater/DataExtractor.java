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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.weld.exceptions.UnsupportedOperationException;

/**
 * Utility class to extract data from buffers, using regex or selectors.
 * 
 * @author Rafael R. Itajuba
 */
public class DataExtractor {

    /**
     * Protect the constructor
     */
    protected DataExtractor() {
        throw new UnsupportedOperationException();
    }

    static final Logger LOGGER = LogManager.getLogger(DataExtractor.class);

    /**
     * Extract data from @content, using @rules. Each rule is identified by its
     * key, that should be used later to identify the result.
     * 
     * @param content
     *            buffer where the raw data is stored
     * @param rules
     *            map of regex rules, that will be matched against content
     * @return a map, containing the keys pointing to the data extracted
     */
    public static Map<String, List<String>> extractWithRegex(
            final String content, final Map<String, String> rules) {

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

    /**
     * Extract data from @content, using @rules. Each rule is identified by its
     * key, that should be used later to identify the result.
     * 
     * @param content
     *            buffer where the raw data is stored
     * @param rules
     *            map of selector, that will be matched against content
     * @return a map, containing the keys pointing to the data extracted
     */
    public static Map<String, List<String>> extractWithSelector(
            final String content, final Map<String, String> rules) {

        // result object for <variable, values>
        Map<String, List<String>> result = new HashMap<>();

        // ...
        Document document = Jsoup.parse(content);

        // for every rule, get its variable identifier 'v'
        rules.forEach((v, r) -> {

            Elements els = document.select(rules.get(v));

            List<String> matches = new LinkedList<>();

            for (Element el : els) {
                matches.add(selectorValue(el));
            }

            result.put(v, matches);
        });

        return result;
    }

    /**
     * This function retrieve a string representation for the element el. If the
     * element has children, then this representation will be a json array.
     * 
     * @param el
     *            root element for this selector
     * @return String representation for the root element @el
     */
    private static String selectorValue(Element el) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();

        String result = "";

        if (el.children().size() == 0) {
            result = el.ownText();
        } else {
            process(el, array, mapper);
            result = array.toString();
        }

        return result;
    }

    /**
     * Process, recursively, the element @el into a @list
     * 
     * @param el
     *            the actual root node of the DOM
     * @param array
     *            the json array where this element should be added
     * @param mapper
     *            is passed down from root call
     */
    private static void process(Element el, ArrayNode array,
            ObjectMapper mapper) {

        if (el.children().size() == 0) {
            if (el.ownText().trim().length() != 0) {
                array.add(el.ownText().trim());
            }
        } else {
            for (Element ec : el.children()) {
                ArrayNode ch = mapper.createArrayNode();

                // This comes first because .build() happens on .add() ;)
                process(ec, ch, mapper);

                if (el.ownText().trim().length() == 0) {
                    array.add(ch);
                } else {
                    array.add(el.ownText().trim());
                }
            }
        }
    }
}
