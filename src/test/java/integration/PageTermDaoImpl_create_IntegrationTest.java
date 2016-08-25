package integration;

import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.dao.*;
import org.hibernate.Session;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-08.
 */
public class PageTermDaoImpl_create_IntegrationTest extends DbTestBase {

    @Test
    public void test_create() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        saveOrUpdate(domain);
        saveOrUpdate(page);
        Session session = sessionFactory.openSession();
        TermDao termDao = new TermDaoImpl(session);
        PageDao pageDao = new PageDaoImpl(session);
        Page p = pageDao.getPage("google.com/a");
        PageTermDao sut = new PageTermDaoImpl(termDao, pageDao, session);
        sut.create(p.getId(), "hello", 1);
    }

}
