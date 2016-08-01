package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.DomainRepositoryImpl;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl_get_domain_exists {

    private DomainRepository sut;
    @Test
    public void get_domain_exists() {

        Db db = new DbImpl(HibernateUtil.getSessionFactory());
        Domain domain = Domain.createFromUrl("www.google.com");
        db.save(domain);
        // the domain has been saved

        Assert.assertNotNull(sut.getDomain("google.com"));
    }

    @Before
    public void setup() {
        sut = new DomainRepositoryImpl(HibernateUtil.getSessionFactory());
    }
}
