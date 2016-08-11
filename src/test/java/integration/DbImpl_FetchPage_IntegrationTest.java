package integration;

import jwm.ir.domain.dao.DomainDao;
import jwm.ir.domain.Page;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbImpl_FetchPage_IntegrationTest extends DbTestBase {

    @Test
    public void test_fetch_page() {
        String pageUrl = "http://localhost/searchengine_test/page1.html";
        DomainDao domainDao = mock(DomainDao.class);
        Page page = Page.create(pageUrl, domainDao);
        db.save(page.getDomain());
        db.save(page);

        Page p = db.getPage(pageUrl);
        Assert.assertEquals(p, page);
    }



}
