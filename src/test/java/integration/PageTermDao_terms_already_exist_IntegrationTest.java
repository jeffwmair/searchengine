package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageTerm;
import jwm.ir.domain.Term;
import jwm.ir.domain.dao.*;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-08.
 */
public class PageTermDao_terms_already_exist_IntegrationTest extends DbTestBase {


    @Test
    public void test_page_link_to_pageterms() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        Term term = new Term("hello");
        save(domain);
        save(page);
        save(term);
        PageTerm pt = new PageTerm(page, term, 1);
        save(pt);

        Session session = sessionFactory.openSession();
        Page p = fetchPageFromDb("google.com/a");
        TermDao termDao = new TermDaoImpl(session);
        PageDao pageDao = new PageDaoImpl(session);
        PageTermDao sut = new PageTermDaoImpl(termDao, pageDao, session);
        Assert.assertTrue(sut.termsAlreadyExist(p.getId()));
        session.close();
    }
}
