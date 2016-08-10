package jwm.ir.service;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.RepositoryFactory;
import jwm.ir.domain.persistence.*;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {

    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final SessionFactory sessionFactory;
    private final RepositoryFactory repositoryFactory;
    public ServiceImpl(SessionFactory sessionFactory, RepositoryFactory repositoryFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url+"; parentUrl"+parentUrl);
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
            log.debug("Domain exists:"+pageDomainName);
            pageDomain = domainRepository.getDomain(pageDomainName);
        }
        else {
            log.debug("Domain does not exist, so creating:"+pageDomainName);
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
    public List<String> getValidDomainExtensions() {
        Session session = sessionFactory.openSession();
        ExtensionDao dao = repositoryFactory.createExtensionDao(session);
        List<String> extensions = dao.getAllValidExtensions();
        session.close();
        return extensions;
    }

    @Override
    public void addDocumentTerms(long pageId, Map<String, Integer> termFrequences) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PageTermDao pageTermDao = repositoryFactory.createPageTermDao(session);

        if (pageTermDao.termsAlreadyExist(pageId)) {
            log.warn("Page terms already exist for page with id '"+pageId+"', so not indexing again");
            return;
        }

        for (Map.Entry<String, Integer> e : termFrequences.entrySet()) {

            String termValue = e.getKey();
            int tf = e.getValue();
            pageTermDao.create(pageId, termValue, tf);

        }

        tx.commit();
        session.close();


    }

}
