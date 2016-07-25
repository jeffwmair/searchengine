package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
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

        Criteria criteriaPages = session.createCriteria(Page.class);
        List<Page> pages = criteriaPages.list();

        Criteria criteriaDomains = session.createCriteria(Domain.class);
        List<Domain> domains = criteriaDomains.list();

        Criteria criterialPageLinks = session.createCriteria(PageLink.class);
        List<PageLink> pageLinks = criterialPageLinks.list();

        for (Page p : pages) System.out.println(p);
        for (Domain p : domains) System.out.println(p);
        for (PageLink p : pageLinks) System.out.println(p);
    }

}
