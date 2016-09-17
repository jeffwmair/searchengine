package integration;

import com.jwm.ir.index.service.ServiceImpl;
import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.dao.DaoFactory;
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
        ServiceImpl sut = new ServiceImpl(sessionFactoryProvider, new DaoFactory());
        sut.addUrlForCrawling(url, parentUrl);

    }

}
