package integration;

import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.SessionFactoryProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbTestBase {

    protected SessionFactoryProvider sessionFactoryProvider;
    protected SessionFactory sessionFactory;

    protected void saveOrUpdate(Object o) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(o);
        tx.commit();
        session.close();
    }

    protected Domain createAndSaveDomain(String domainName) {
        Domain d = Domain.createFromUrl(domainName);
        saveOrUpdate(d);
        return d;
    }

    protected Page createAndSavePage(String url, Domain d) {
        Page p = createTransientPage(url);
        p.setDomain(d);
        saveOrUpdate(p);
        return p;
    }

    protected Page createTransientPage(String url) {
        Domain d = Domain.createFromUrl(url);
        Page p = Page.create(d, url);
        return p;
    }

    protected Page fetchPageFromDb(String url) {
        Session session = sessionFactory.openSession();
        Page p = (Page)session.createCriteria(Page.class).add(Restrictions.eq("url", url)).uniqueResult();
        session.close();
        return p;
    }

    protected void saveNewPageWithDomain(Page p) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(p.getDomain());
        session.save(p);
        tx.commit();
        session.close();
    }

    @Before
    public void setup() {
        sessionFactoryProvider = new SessionFactoryProvider(SessionFactoryProvider.Mode.Test);
        sessionFactory = sessionFactoryProvider.getSessionFactory();
    }
}
