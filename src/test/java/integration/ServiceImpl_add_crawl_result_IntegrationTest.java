package integration;

import jwm.ir.domain.Page;
import jwm.ir.domain.RepositoryFactory;
import jwm.ir.service.ServiceImpl;
import jwm.ir.utils.HibernateUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-10.
 */
public class ServiceImpl_add_crawl_result_IntegrationTest extends DbTestBase {

    @Test
    public void add_Crawl_result_to_db_with_correct_fail_count() {

        String url = "google.com/a";
        Page p = createTransientPage(url);
        saveNewPageWithDomain(p);

        ServiceImpl sut = new ServiceImpl(HibernateUtil.getSessionFactory(), new RepositoryFactory());
        sut.addCrawlResult(url, "title", "desc", Page.CrawlResult.Fail);

        Page p_again = fetchPageFromDb(url);
        Assert.assertEquals(p_again.getFailCount(), 1);

    }

}
