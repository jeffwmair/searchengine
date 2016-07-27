package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbImpl_FetchPage_IntegrationTest extends DbTestBase {

    @Test
    public void test_fetch_page() {
        Domain domain = new Domain("localhost/searchengine_test", 1);
        String pageUrl = "http://localhost/searchengine_test/page1.html";
        Page page = new Page(domain, pageUrl);
        db.save(domain);
        db.save(page);

        Page p = db.getPage(pageUrl);
        Assert.assertEquals(p, page);
    }

}
