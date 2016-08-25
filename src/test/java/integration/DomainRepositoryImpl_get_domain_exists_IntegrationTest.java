package integration;

import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.dao.DomainDaoImpl;
import com.jwm.ir.persistence.HibernateUtil;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl_get_domain_exists_IntegrationTest extends DbTestBase {

    @Test
    public void get_domain_exists() {

        Domain domain = Domain.createFromUrl("www.google.com");
        saveOrUpdate(domain);

        Session session = HibernateUtil.getSessionFactory().openSession();
        DomainDaoImpl sut = new DomainDaoImpl(session);
        Domain d = sut.getDomain("google.com");
        session.close();
        Assert.assertNotNull(d);
    }
}
