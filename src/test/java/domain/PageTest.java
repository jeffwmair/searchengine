package domain;

import jwm.ir.domain.Page;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class PageTest {

    @Test
    public void test_create_page_from_url_with_domain() {
        String url = "http://www.google.com/foo";
        Page page = Page.create(url);

        Assert.assertEquals(url, page.getUrl());
        Assert.assertEquals("google.com", page.getDomain().getDomain());
    }
}
