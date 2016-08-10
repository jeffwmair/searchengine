package integration;

import jwm.ir.domain.ValidExtension;
import jwm.ir.domain.persistence.ExtensionDao;
import jwm.ir.domain.persistence.ExtensionDaoImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-08-09.
 */
public class ExtensionDao_get_all_extensions_IntegrationTest {

    private SessionFactory sessionFactory;

    @Test
    public void get_all_extensions_test() {
        addValidExtensions();
        ExtensionDao sut = new ExtensionDaoImpl(sessionFactory.openSession());
        List<String> validExtensions = sut.getAllValidExtensions();
        Assert.assertEquals("should contain 3 extensions", 3, validExtensions.size());
        Assert.assertTrue("should contain 'ca' extension", validExtensions.contains("ca"));
    }

    @Before
    public void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    private void addValidExtensions() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        String[] extensions = {"com", "ca", "me"};
        for (String ext : extensions) {
            ValidExtension validExtension = new ValidExtension(1, ext);
            session.save(validExtension);
        }
        tx.commit();
        session.close();

    }
}