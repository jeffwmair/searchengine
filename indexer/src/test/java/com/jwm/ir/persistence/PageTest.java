package com.jwm.ir.persistence;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class PageTest {

    @Test
    public void try_setting_pagerank_when_not_verified_throws() {
       try {
           // not verified, by default
           Page p = new Page();
           p.setPageRank(1);
           Assert.fail("should have thrown an exception");
       }
       catch (IllegalStateException ex) {}
    }

    @Test
    public void increment_fail_count_from_zero_should_not_go_negative() {
        Page page = new Page();
        page.updateFailCount(Page.CrawlResult.Success);
        Assert.assertEquals(0, page.getFailCount());

    }

    @Test
    public void test_create_page_with_domain() {
        String url = "http://www.google.com/foo";
        Page page = new Page(url, Page.MakeNewDomain.Yes);
        Assert.assertEquals("google.com", page.getDomain().getDomain());
    }

    @Test
    public void test_create_page_without_domain() {
        String url = "http://www.google.com/foo";
        Page page = new Page(url, Page.MakeNewDomain.No);
        Assert.assertNull(page.getDomain());
    }
}
