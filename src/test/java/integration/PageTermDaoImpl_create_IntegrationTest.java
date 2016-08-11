package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.dao.*;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-08.
 */
public class PageTermDaoImpl_create_IntegrationTest {

    SessionFactory sessionFactory;
    PageTermDao pageTermDao;
    Db db;

    @Test
    public void test_create() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        db.save(domain);
        db.save(page);
        Session session = sessionFactory.openSession();
        TermDao termDao = new TermDaoImpl(session);
        PageDao pageDao = new PageDaoImpl(session);
        Page p = pageDao.getPage("google.com/a");
        pageTermDao = new PageTermDaoImpl(termDao, pageDao, session);
        pageTermDao.create(p.getId(), "hello", 1);
    }

    @Before
    public void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
    }
}
