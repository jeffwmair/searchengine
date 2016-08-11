package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbTestBase {

    protected SessionFactory sessionFactory;
    protected Db db;

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
        sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
    }
}
