package integration;

import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.PageTerm;
import com.jwm.ir.persistence.Term;
import com.jwm.ir.persistence.dao.*;
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
        saveOrUpdate(domain);
        saveOrUpdate(page);
        saveOrUpdate(term);
        PageTerm pt = new PageTerm(page, term, 1);
        saveOrUpdate(pt);

        Session session = sessionFactory.openSession();
        Page p = fetchPageFromDb("google.com/a");
        TermDao termDao = new TermDaoImpl(session);
        PageDao pageDao = new PageDaoImpl(session);
        PageTermDao sut = new PageTermDaoImpl(termDao, pageDao, session);
        Assert.assertTrue(sut.termsAlreadyExist(p.getId()));
        session.close();
    }
}
