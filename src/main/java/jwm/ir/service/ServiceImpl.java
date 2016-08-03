package jwm.ir.service;

import jwm.ir.domain.*;
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
    private final UrlAddService urlAddService;
    public ServiceImpl(SessionFactory sessionFactory, UrlAddService urlAddService) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
        this.urlAddService = urlAddService;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url);
        }


        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        UnitOfWork unitOfWork = new UnitOfWork(session);
        DomainRepository domainRepository = new DomainRepositoryImpl(unitOfWork);
        PageRepository pageRepository = new PageRepositoryImpl(unitOfWork);
        PageLinkRepository pageLinkRepository = new PageLinkRepositoryImpl(unitOfWork);

        // only run if the page doesn't exist
        if (!pageRepository.pageExists(url)) {
            urlAddService.addUrlForCrawling(url, parentUrl,
                    pageRepository,
                    domainRepository,
                    pageLinkRepository);
        }

        unitOfWork.persist();

        tx.commit();
        session.close();
        if (log.isDebugEnabled()) {
            log.debug("Completed to add url for crawling:" + url);
        }

    }

}
