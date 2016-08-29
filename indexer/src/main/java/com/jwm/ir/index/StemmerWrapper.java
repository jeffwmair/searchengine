package com.jwm.ir.index;

import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by Jeff on 2016-08-14.
 */
public class StemmerWrapper {



    public static String stem(String term) {
        String trimmed = term.trim();

        if (shouldKeepSame(trimmed)) {
            return trimmed;
        }

        Stemmer stemmer = new Stemmer();
        stemmer.add(trimmed.toCharArray(), trimmed.length());
        stemmer.stem();
        return stemmer.toString();
    }

    private static Collection keep_same = new ArrayList();

    private static boolean shouldKeepSame(String val) {
        return keep_same.contains(val);
    }

    static {
        // todo: fill this out as needed
        keep_same.add("this");
    }

    public static Map<String, Integer> convertToStemmed(Map<String, Integer> unstemmed) {
        Assert.notNull(unstemmed);
        Map<String, Integer> stemmed = new HashMap<>();
        for(String term : unstemmed.keySet()) {
            stemmed.put(stem(term), unstemmed.get(term));
        }
        return stemmed;
    }
}
