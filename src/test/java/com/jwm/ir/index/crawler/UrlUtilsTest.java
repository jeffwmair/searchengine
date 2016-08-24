package com.jwm.ir.index.crawler;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-20.
 */
public class UrlUtilsTest {


    @Test
    public void getUrlFromHttpDomainOnlyTest() {
        assertGetDomainAndRobots("http://jwm.ir.domain.com");
    }

    @Test
    public void getUrlFromDomainOnlyTest() {
        assertGetDomainAndRobots("jwm.ir.domain.com");
    }

    @Test
    public void getUrlFromWwwDomainOnlyTest() {
        assertGetDomainAndRobots("www.jwm.ir.domain.com");
    }

    @Test
    public void getFromPageUrlTest() {
        assertGetDomainAndRobots("http://jwm.ir.domain.com/foo.html");
    }

    @Test
    public void getFromTrailingSlashDomain() {
        assertGetDomainAndRobots("http://jwm.ir.domain.com/");
    }

    @Test
    public void getFromTrailingSlashDomainPlusPage() {
        assertGetDomainAndRobots("http://jwm.ir.domain.com/foo/");
    }

    @Test
    public void getFromHttpsProtocol() {
        assertGetDomainAndRobots("https://jwm.ir.domain.com/foo/bar/page.html");
    }

    @Test
    public void getFromThreeDeepPage() {
        assertGetDomainAndRobots("http://jwm.ir.domain.com/foo/bar/page.html");
    }

    @Test
    public void getDomainFromWordNotExtensionNotLocalhostThrowsIllegalArgException() {
        try {
            UrlUtils.getDomainFromAbsoluteUrl("foobar");
            Assert.fail("should throw illegal arg exception");
        }
        catch (IllegalArgumentException e) {}
    }

    @Test
    public void getDomainFromUrlNullThrowsIllegalArgException() {
        try {
            UrlUtils.getDomainFromAbsoluteUrl(null);
            Assert.fail("should throw illegal arg exception");
        }
        catch (NullPointerException e) {}
    }


    private void assertGetDomainAndRobots(String url) {
        Assert.assertEquals("jwm.ir.domain.com", UrlUtils.getDomainFromAbsoluteUrl(url));
        Assert.assertEquals("jwm.ir.domain.com/robots.txt", UrlUtils.getRobotsTxtUrl(url));
    }
}
