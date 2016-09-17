package com.jwm.ir.index;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-14.
 */
public class StemmerWrapperTest {

    @Test
    public void convert_set_to_stemmed() {
        Map<String,Integer> unstemmed = new HashMap<>();
        unstemmed.put("dogs", 1);
        unstemmed.put("running", 1);
        unstemmed.put("cats", 2);

        Map<String, Integer> stemmed = StemmerWrapper.convertToStemmed(unstemmed);
        Assert.assertEquals(3, stemmed.size());
        Assert.assertTrue("should convert dogs to dog", stemmed.containsKey("dog"));
        Assert.assertTrue("should convert cats to cat", stemmed.containsKey("cat"));
        Assert.assertTrue("should convert running to run", stemmed.containsKey("run"));
    }

    @Test
    public void special_this_same() {
        String stemmed = StemmerWrapper.stem("this");
        Assert.assertEquals("this", stemmed);
    }

    @Test
    public void verb_present_ing() {
        String stemmed = StemmerWrapper.stem("running");
        Assert.assertEquals("run", stemmed);
    }

    @Test
    public void plural_simple() {
        String stemmed = StemmerWrapper.stem("dogs");
        Assert.assertEquals("dog", stemmed);
    }

    @Test
    public void verb_ed() {
        String stemmed = StemmerWrapper.stem("dogged");
        Assert.assertEquals("dog", stemmed);
    }
}
