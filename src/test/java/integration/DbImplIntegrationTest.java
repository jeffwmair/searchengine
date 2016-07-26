package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImplIntegrationTest {

    @Test
    public void test_popUrls_should_remove_pages_from_db_after_reading() {

        // first some some stuff in the db
        SessionFactory fact = HibernateUtil.getSessionFactory();
        Session session = fact.openSession();
        Transaction tx = session.beginTransaction();

        Domain domain = new Domain("localhost", 1);
        Page page1 = new Page(domain, "foobar.html");
        Page page2 = new Page(domain, "foobar2.html");
        session.save(domain);
        session.save(page1);
        session.save(page2);
        tx.commit();
        session.close();
        // done inserting data

        Db db = new DbImpl(fact);
        List<String> urls = db.popUrls();

        Assert.assertEquals("Should have popped off 2 urls", 2, urls.size());
        Assert.assertEquals("Should be no more urls to pop from db", 0, db.popUrls().size());



    }
}
