package integration;

import jwm.ir.domain.Page;
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
        Criteria criteria = session.createCriteria(Page.class);
        List<Page> pages = criteria.list();

        for (Page p : pages) {
            System.out.println(p);
        }


    }
}
