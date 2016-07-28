package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImpl_PopUrls_IntegrationTest extends DbTestBase {

    @Test
    public void test_popUrls_should_remove_pages_from_db_after_reading() {

        // first some some stuff in the db
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Domain domain = Domain.createFromUrl("http://localhost/foobar.html");
        Page page1 = Page.create(domain, "foobar.html");
        Page page2 = Page.create(domain, "foobar2.html");
        session.save(domain);
        session.save(page1);
        session.save(page2);
        tx.commit();
        session.close();
        // done inserting data

        List<String> urls = db.popUrls();

        Assert.assertEquals("Should have popped off 2 urls.  First url found:"+urls.get(0), 2, urls.size());
        Assert.assertEquals("Should be no more urls to pop from db", 0, db.popUrls().size());

    }

}
