package jwm.ir.utils;

import jwm.ir.domain.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImpl implements Db {

    private static final Logger log = LogManager.getLogger(DbImpl.class);
    private final SessionFactory sessionFactory;
    public DbImpl(SessionFactory sessionFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide a sessionFactory");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<String> popUrls() {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(Page.class);
        List<Page> pages = null;

        try {

            pages = criteria.list();
        }
        catch (Exception e) {
            tx.commit();
            log.error(e);
            return new ArrayList<>();
        }

        List<String> urls = new ArrayList<>();
        for(Page p : pages) {
            urls.add(p.getUrl());
            session.delete(p);
        }

        tx.commit();
        session.close();
        return urls;
    }
}
