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
        Criteria domainCrit = session.createCriteria(Domain.class);
        List<Domain> domains = domainCrit.list();
        for (Domain x : domains) { System.out.println(x); }

    }

}
