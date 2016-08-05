package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.RepositoryFactory;
import jwm.ir.service.ServiceImpl;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl_add_url_for_crawling_domain_already_exists_should_not_add_again_IntegrationTest {
    private ServiceImpl sut;
    private Db db;

    @Test
    public void add_url_for_crawling_domain_already_exists_should_not_add_again() {

        // arrange
        // page a points to b
        String parentUrl = "www.google.com/a";
        String url = "www.google.com/b";
        Domain domain = Domain.createFromUrl(url);
        Page parent = Page.create(domain, parentUrl);
        db.save(domain);
        db.save(parent);

        // act
        sut.addUrlForCrawling(url, parentUrl);

    }

    @Before
    public void setup() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
        sut = new ServiceImpl(sessionFactory, new RepositoryFactory());
    }
}
