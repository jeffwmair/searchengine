package integration;

import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.PageTerm;
import com.jwm.ir.persistence.Term;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-05.
 */
public class PageTerm_UniquePageTerm_IntegrationTest extends DbTestBase {

    @Test
    public void try_insert_duplicate_throws_exception() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/foo");
        Term term = new Term("hiya");

        PageTerm pt1 = new PageTerm();
        pt1.setPage(page);
        pt1.setTerm(term);

        PageTerm pt2 = new PageTerm();
        pt2.setPage(page);
        pt2.setTerm(term);

        session.save(domain);
        session.save(page);
        session.save(term);
        session.save(pt1);

        try {
            session.save(pt2);
            Assert.fail("Should throw duplicate entry exception");
        }
        catch (ConstraintViolationException ex) {
            return;
        }

        tx.commit();
        session.close();

    }
}
