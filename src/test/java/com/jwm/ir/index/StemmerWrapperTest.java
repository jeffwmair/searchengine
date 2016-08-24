package com.jwm.ir.index;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-14.
 */
public class StemmerWrapperTest {

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
