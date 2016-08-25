package integration;

import com.jwm.ir.persistence.dao.DomainDao;
import com.jwm.ir.persistence.dao.DomainDaoImpl;
import com.jwm.ir.persistence.HibernateUtil;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl_get_domain_not_exists_throws_IntegrationTest {

    @Test
    public void get_domain_not_exists_throws() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        DomainDao sut = new DomainDaoImpl(session);
        try {
            Assert.assertNotNull(sut.getDomain("google.com"));
            Assert.fail("Should throw an exception!");
        }
        catch (IllegalArgumentException ex) { }
        finally {
            session.close();
        }

    }
}
