package com.jwm.ir.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-14.
 */
public class QueryHelper {

    /**
     * Converts the given query string into a map of the terms and frequency of occurrence of the term, from the query string.
     * @param query
     * @return
     */
    public static Map<String, Integer> getMapOfQueryTerms(String query) {

        Map<String,Integer> result = new HashMap<>();
        String[] terms = query.split(" ");
        for(String term : terms) {
            int instances = 1;
            if (result.containsKey(term)) {
                instances += result.get(term).intValue();
            }
            result.put(term, instances);
        }

        return result;
    }
}
