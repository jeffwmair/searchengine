package integration;

import jwm.ir.crawler.UrlFeed;
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

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeedIntegrationTest {

    @Test
    public void testUrlFeed() {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        // add some dummy db records
        int crawlerId = 1;
        Domain domain = new Domain("localhost", crawlerId);
        Page page1 = new Page(domain, "http://localhost/searchengine_test/page1.html");
        Page page2 = new Page(domain, "http://localhost/searchengine_test/page2.html");
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(domain);
        session.save(page1);
        session.save(page2);
        tx.commit();
        session.close();

        Db db = new DbImpl(sessionFactory);
        BlockingQueue<String> output = new LinkedBlockingQueue<>();
        UrlFeed sut = new UrlFeed(db, output);

        // process the records
        sut.process();

        Assert.assertTrue("http://localhost/searchengine_test/page1.html".equals(output.remove()));
        Assert.assertTrue("http://localhost/searchengine_test/page2.html".equals(output.remove()));
        try {
            output.remove();
            Assert.fail("should throw a NPE; only 2 items in the queue");
        }
        catch (NoSuchElementException ex) { }

    }
}
