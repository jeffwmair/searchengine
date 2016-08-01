package jwm.ir.service;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Db;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {


    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final Db db;
    private final Service innerService;
    private final SessionFactory sessionFactory;
    public ServiceImpl(Db db, Service innerService, SessionFactory sessionFactory) {
        AssertUtils.notNull(db, "Must provide a db instance");
        AssertUtils.notNull(db, "Must provide a sessionFactory instance");
        this.db = db;
        this.sessionFactory = sessionFactory;
        this.innerService = innerService;
    }

    @Override
    public AddUrlForCrawlingDto addUrlForCrawling(String url, String parentUrl) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        AddUrlForCrawlingDto dto = innerService.addUrlForCrawling(url, parentUrl);

        // persist here?

        tx.commit();
        session.close();

        return null;
    }
}
