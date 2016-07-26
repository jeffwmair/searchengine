package jwm.ir.utils;

import jwm.ir.domain.Page;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImpl implements Db {

    private final SessionFactory sessionFactory;
    public DbImpl(SessionFactory sessionFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide a sessionFactory");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<String> getUrls() {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Page.class);
        List<Page> pages = criteria.list();

        List<String> urls = new ArrayList<>();

        for(Page p : pages) {
            urls.add(p.getUrl());
        }

        session.close();
        return urls;
    }
}
