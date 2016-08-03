package integration;

import jwm.ir.domain.Domain;
import jwm.ir.service.ServiceImpl;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl_add_url_for_crawling_domain_already_exists_should_not_add_again {
    private ServiceImpl sut;
    private Db db;

    @Test
    public void add_url_for_crawling_domain_already_exists_should_not_add_again() {

        // arrange
        // page a points to b
        String parentUrl = "www.google.com/a";
        String url = "www.google.com/b";
        Domain domain = Domain.createFromUrl(url);
        db.save(domain);

        // act
        sut.addUrlForCrawling(url, parentUrl);

    }

    @Before
    public void setup() {
        db = new DbImpl(HibernateUtil.getSessionFactory());
    }
}
