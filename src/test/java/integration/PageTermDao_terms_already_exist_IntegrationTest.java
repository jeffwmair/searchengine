package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageTerm;
import jwm.ir.domain.Term;
import jwm.ir.domain.dao.*;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-08.
 */
public class PageTermDao_terms_already_exist_IntegrationTest {

    SessionFactory sessionFactory;
    PageTermDao pageTermDao;
    Db db;

    @Test
    public void test_page_link_to_pageterms() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        Term term = new Term("hello");
        db.save(domain);
        db.save(page);
        db.save(term);
        PageTerm pt = new PageTerm(page, term, 1);
        db.save(pt);

        Session session = sessionFactory.openSession();
        Page p = db.getPage("google.com/a");
        TermDao termDao = new TermDaoImpl(session);
        PageDao pageDao = new PageDaoImpl(session);
        pageTermDao = new PageTermDaoImpl(termDao, pageDao, session);
        Assert.assertTrue(pageTermDao.termsAlreadyExist(p.getId()));
        session.close();
    }

    @Before
    public void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
    }
}
