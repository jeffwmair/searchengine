package integration;

import jwm.ir.domain.Page;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbImpl_FetchPage_IntegrationTest extends DbTestBase {

    @Test
    public void test_fetch_page() {
        String pageUrl = "http://localhost/searchengine_test/page1.html";
        Page page = new Page(pageUrl, Page.MakeNewDomain.Yes);
        save(page.getDomain());
        save(page);
        Page p = fetchPageFromDb(pageUrl);
        Assert.assertEquals(p, page);
    }



}
