package integration;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.DomainRepositoryImpl;
import jwm.ir.utils.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl_get_domain_not_exists_throws_IntegrationTest {

    private DomainRepository sut;
    @Test
    public void get_domain_not_exists_throws() {

        try {
            Assert.assertNotNull(sut.getDomain("google.com"));
            Assert.fail("Should throw an exception!");
        }
        catch (NullPointerException ex) { }

    }

    @Before
    public void setup() {
        sut = new DomainRepositoryImpl(HibernateUtil.getSessionFactory().openSession());
    }
}