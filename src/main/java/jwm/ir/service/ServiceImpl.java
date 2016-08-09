package jwm.ir.service;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.RepositoryFactory;
import jwm.ir.domain.persistence.DomainRepository;
import jwm.ir.domain.persistence.PageLinkRepository;
import jwm.ir.domain.persistence.PageRepository;
import jwm.ir.domain.persistence.PageTermDao;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {

    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final SessionFactory sessionFactory;
    private final RepositoryFactory repositoryFactory;
    public ServiceImpl(SessionFactory sessionFactory,
                       RepositoryFactory repositoryFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url);
        }


        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();


        DomainRepository domainRepository = repositoryFactory.createDomainRepository(session);
        PageRepository pageRepository = repositoryFactory.createPageRepository(session);
        PageLinkRepository pageLinkRepository = repositoryFactory.createPageLinkRepository(session);

        // only run if the page doesn't exist
        if (pageRepository.pageExists(url)) {
            log.warn("Page with url '"+url+"' already exists in the database, so not adding");
            tx.rollback();
            return;
        }

        Page page = pageRepository.create(url, domainRepository);
        Domain pageDomain;
        final String pageDomainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        if (domainRepository.domainExists(pageDomainName)) {
            pageDomain = domainRepository.getDomain(pageDomainName);
        }
        else {
            pageDomain = domainRepository.create(pageDomainName);
        }

        page.setDomain(pageDomain);

        // we assume that the parent page must have already been indexed; how else can it be the parent?
        Page referantPage = pageRepository.getPage(parentUrl);

        pageLinkRepository.create(page, referantPage);

        tx.commit();
        session.close();

        if (log.isDebugEnabled()) {
            log.debug("Completed to add url for crawling:" + url);
        }

    }

    @Override
    public void addDocumentTerms(long pageId, Map<String, Integer> termFrequences) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PageTermDao pageTermDao = repositoryFactory.createPageTermDao(session);

        for (Map.Entry<String, Integer> e : termFrequences.entrySet()) {

            String termValue = e.getKey();
            int tf = e.getValue();
            pageTermDao.create(pageId, termValue, tf);

        }

        tx.commit();
        session.close();


    }

}
