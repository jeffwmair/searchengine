package integration;

import jwm.ir.entity.Domain;
import jwm.ir.entity.Page;
import jwm.ir.entity.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl_add_url_for_crawling_domain_already_exists_should_not_add_again_IntegrationTest extends DbTestBase {
    @Test
    public void add_url_for_crawling_domain_already_exists_should_not_add_again() {

        // arrange
        // page a points to b
        String parentUrl = "www.google.com/a";
        String url = "www.google.com/b";
        Domain domain = Domain.createFromUrl(url);
        Page parent = Page.create(domain, parentUrl);
        saveOrUpdate(domain);
        saveOrUpdate(parent);

        // act
        ServiceImpl sut = new ServiceImpl(sessionFactory, new DaoFactory());
        sut.addUrlForCrawling(url, parentUrl);

    }

}
