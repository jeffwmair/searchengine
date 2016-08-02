package jwm.ir.service;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.DomainRepositoryImpl;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {


    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final SessionFactory sessionFactory;
    public ServiceImpl(SessionFactory sessionFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url);
        }
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        DomainRepository domainRepository = new DomainRepositoryImpl(session);

        Page page = Page.create(url, domainRepository);

        if (page.getDomain().getId() == 0) {
            session.save(page.getDomain());
        }

        Page parentPage = Page.create(parentUrl, domainRepository);

        if (parentPage.getDomain().getId() == 0) {
            session.save(parentPage.getDomain());
        }

        session.save(page);
        session.save(parentPage);

        PageLink pageLink = PageLink.create(parentPage, page);
        session.save(pageLink);

        tx.commit();
        session.close();
        if (log.isDebugEnabled()) {
            log.debug("Completed to add url for crawling:" + url);
        }

    }
}
