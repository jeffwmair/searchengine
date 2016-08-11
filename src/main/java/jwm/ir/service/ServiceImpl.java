package jwm.ir.service;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.DaoFactory;
import jwm.ir.domain.persistence.*;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {

    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final SessionFactory sessionFactory;
    private final DaoFactory daoFactory;
    public ServiceImpl(SessionFactory sessionFactory, DaoFactory daoFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
        this.daoFactory = daoFactory;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url+"; parentUrl"+parentUrl);
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        DomainRepository domainRepository = daoFactory.createDomainRepository(session);
        PageRepository pageRepository = daoFactory.createPageRepository(session);
        PageLinkRepository pageLinkRepository = daoFactory.createPageLinkRepository(session);

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
    public void addCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PageRepository pageRepository = daoFactory.createPageRepository(session);

        pageRepository.setPageCrawlResult(url, pageTitle, pageDesc, result);

        tx.commit();
        session.close();
    }

    @Override
    public List<String> getValidDomainExtensions() {
        Session session = sessionFactory.openSession();
        ExtensionDao dao = daoFactory.createExtensionDao(session);
        List<String> extensions = dao.getAllValidExtensions();
        session.close();
        return extensions;
    }

    @Override
    public List<String> getUrlsToCrawl() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(Page.class);
        List<Page> pages;

        try {
            pages = criteria.add(Restrictions.isNull("lastCrawl")).list();
        }
        catch (Exception e) {
            tx.commit();
            log.error(e);
            return new ArrayList<>();
        }

        List<String> urls = new ArrayList<>();

        for(Page p : pages) {
            urls.add(p.getUrl());
            //session.delete(p);
        }

        tx.commit();
        session.close();
        log.debug("Popped Urls:"+urls+", size:"+urls.size());
        return urls;
    }

    @Override
    public void updatePageRanks(Map<Long, Double> pageRanks) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        PageRepository pageRepository = daoFactory.createPageRepository(session);
        log.info("Beginning to update pageranks for " + pageRanks.size() + " pages");
        pageRepository.updatePageRanks(pageRanks);
        log.info("Completed updating pageranks");

        tx.commit();
        session.close();
    }

    @Override
    public void addDocumentTerms(long pageId, Map<String, Integer> termFrequences) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        PageTermDao pageTermDao = daoFactory.createPageTermDao(session);

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

    @Override
    public List<Long> getPageIdsGreaterThanPageId(long lastPageReceived) {
        Session session = sessionFactory.openSession();
        List<Page> pages = session.createCriteria(Page.class).add(Restrictions.gt("id", lastPageReceived)).list();
        List<Long> pageIds = new ArrayList<>();
        for (Page p : pages) {
            pageIds.add(p.getId());
        }
        session.close();
        return pageIds;
    }

    @Override
    public List<Page> getAllPages() {
        Session session = sessionFactory.openSession();
        List<Page> pages = session.createCriteria(Page.class).list();
        session.close();
        return pages;
    }

}
