package integration;

import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.dao.DomainDaoImpl;
import com.jwm.ir.persistence.dao.PageDaoImpl;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-25.
 */
public class PageDaoImpl_get_indexed_page_count extends DbTestBase {

    @Test
    public void get_indexed_count() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PageDaoImpl pageDaoForSetup = new PageDaoImpl(session);
        DomainDaoImpl domainDao = new DomainDaoImpl(session);
        Page newPage = pageDaoForSetup.create("google.com/a", domainDao);
        session.save(newPage.getDomain());
        newPage.setIsVerified();
        session.update(newPage);
        Map<Long, Double> pageRanks = new HashMap<>();
        pageRanks.put(newPage.getId(), 0.5);
        pageDaoForSetup.updatePageRanks(pageRanks);
        tx.commit();
        session.close();

        session = sessionFactory.openSession();
        PageDaoImpl sut = new PageDaoImpl(session);
        Assert.assertEquals("should be 1 indexed page", 1, sut.getIndexedPageCount());
        session.close();

    }
}
