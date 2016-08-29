package com.jwm.ir.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Jeff on 2016-08-14.
 */
public class QueryHelperTest {

    @Test
    public void duplicate_term_map_only_once() {
        Map<String, Integer> map = QueryHelper.getMapOfQueryTerms("hello hello");
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void duplicate_term_show_number_of_instances() {
        Map<String, Integer> map = QueryHelper.getMapOfQueryTerms("hello hello");
        Assert.assertEquals(2, map.get("hello").intValue());
    }
}
