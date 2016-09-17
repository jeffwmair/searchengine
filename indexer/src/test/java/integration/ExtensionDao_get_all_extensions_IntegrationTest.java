package integration;

import com.jwm.ir.persistence.ValidExtension;
import com.jwm.ir.persistence.dao.ExtensionDao;
import com.jwm.ir.persistence.dao.ExtensionDaoImpl;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-08-09.
 */
public class ExtensionDao_get_all_extensions_IntegrationTest extends DbTestBase {

    @Test
    public void get_all_extensions_test() {
        addValidExtensions();
        ExtensionDao sut = new ExtensionDaoImpl(sessionFactory.openSession());
        List<String> validExtensions = sut.getAllValidExtensions();
        Assert.assertEquals("should contain 3 extensions", 3, validExtensions.size());
        Assert.assertTrue("should contain 'ca' extension", validExtensions.contains("ca"));
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
