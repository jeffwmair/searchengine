package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Jeff on 2016-07-19.
 */
abstract class IntegrationTestBase {

    enum RunCrawler { Yes, No };
    enum RunIndexer { Yes, No };
    enum RobotTxtState { Deny, Accept };

    void setupDb() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Domain domainLocalhost = new Domain("localhost", 1);
        Page page1 = new Page(domainLocalhost, "http://localhost/searchengine_test/page1.html");

        Transaction tx = session.beginTransaction();
        session.save(domainLocalhost);
        session.save(page1);
        tx.commit();
    }

    void deployPhpServicesToApache() {
        throw new RuntimeException("Not impl");
    }

    void deployRobotsTxtFile(RobotTxtState state) {
        throw new RuntimeException("Not impl");
    }

    void deployWebPagesToBeIndexed() {
        throw new RuntimeException("Not impl");
    }

    void startProgram(RunCrawler runCrawler, RunIndexer runIndexer) {

    }
}
