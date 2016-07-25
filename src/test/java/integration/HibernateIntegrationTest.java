package integration;

import jwm.ir.domain.*;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-07-23.
 */
public class HibernateIntegrationTest {

    @Test
    public void startupWithHibernateTest() {

        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        List<Page> pages = session.createCriteria(Page.class).list();
        List<PageSubmission> pageSubmissions = session.createCriteria(PageSubmission.class).list();
        List<Domain> domains = session.createCriteria(Domain.class).list();
        List<PageLink> pageLinks = session.createCriteria(PageLink.class).list();

        for (Page p : pages) System.out.println(p);
        for (PageSubmission p : pageSubmissions) System.out.println(p);
        for (Domain p : domains) System.out.println(p);
        for (PageLink p : pageLinks) System.out.println(p);

    }

}
