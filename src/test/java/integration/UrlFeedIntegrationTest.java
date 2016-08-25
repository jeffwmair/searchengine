package integration;

import com.jwm.ir.persistence.HibernateUtil;
import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.dao.DaoFactory;
import com.jwm.ir.index.crawler.UrlFeed;
import com.jwm.ir.index.crawler.UrlFeedRunner;
import com.jwm.ir.index.service.Service;
import com.jwm.ir.index.service.ServiceImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeedIntegrationTest {

    @Test
    public void testUrlFeed() {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        if (sessionFactory.isClosed()) sessionFactory.openSession();

        // add some dummy db records
        Domain domain = Domain.createFromUrl("localhost");
        Page page1 = Page.create(domain, "http://localhost/searchengine_test/page1.html");
        Page page2 = Page.create(domain, "http://localhost/searchengine_test/page2.html");
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(domain);
        session.save(page1);
        session.save(page2);
        tx.commit();
        session.close();

        Service service = new ServiceImpl(sessionFactory, new DaoFactory());
        BlockingQueue<String> output = new LinkedBlockingQueue<>();
        UrlFeed sut = new UrlFeed(service, output);

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

    /**
     * Not an integration test per se; can just be run manually
     * to try out the UrlFeed/Runner
     * @param args
     */
    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Service service = new ServiceImpl(sessionFactory, new DaoFactory());
        final BlockingQueue<String> out = new LinkedBlockingQueue<>();
        UrlFeed feeder = new UrlFeed(service, out);
        UrlFeedRunner x = new UrlFeedRunner(executorService, feeder);
        x.start();


        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        System.out.println(out.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(listener);
        t.start();
    }
}
