package com.jwm.ir.persistence;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class DomainTest {

    @Test
    public void test_create_domain_object_from_page_url() {

        String url = "http://www.google.com/fooo.html";
        Domain d = Domain.createFromUrl(url);
        Assert.assertEquals("google.com", d.getDomain());
    }
}
