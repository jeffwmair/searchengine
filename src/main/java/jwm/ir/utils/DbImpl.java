package jwm.ir.utils;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.domain.RepositoryFactory;
import jwm.ir.domain.persistence.PageRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImpl implements Db {

    private final Db phpDb;
    private static final Logger log = LogManager.getLogger(DbImpl.class);
    private final SessionFactory sessionFactory;

    public DbImpl(SessionFactory sessionFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide a sessionFactory");
        this.sessionFactory = sessionFactory;
        this.phpDb = new Database("localhost/searchengine");
    }

    @Override
    public Page getPage(String url) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Page.class);
        criteria.add(Restrictions.eq("url", url));
        Object result = criteria.uniqueResult();
        AssertUtils.notNull(result, "Page with url '"+url+"' could not be found in the database!");
        return (Page)result;
    }

    @Override
    public void addPerformanceStats(int verifications, int crawls, int indexes) {
        phpDb.addPerformanceStats(verifications, crawls, indexes);
    }

    @Override
    public void updateSummaries() {
        phpDb.updateSummaries();
    }

    @Override
    public void addDocumentTerms(String json, long pageId) {
        phpDb.addDocumentTerms(json, pageId);
    }

    @Override
    public List<String> getPageLinks(List<String> pageIds) {

        if (pageIds.isEmpty()) {
            log.warn("getPageLinks: Empty list of pageIds provided, so returning empty list");
            return new ArrayList<>();
        }

        // todo: change the interface to ids (long), or better yet (maybe) urls
        log.debug("getPageLinks for pageIds:"+pageIds.toString());
        List<Long> pageIds_long = new ArrayList<>();
        for (String s : pageIds) {
            log.debug("\tgetPageLinks for pageId:"+s);
            pageIds_long.add(Long.parseLong(s));
        }

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<String> allLinks = new ArrayList<>();
        Criteria crit = session.createCriteria(PageLink.class);
        log.debug("creating criteria");
        List<PageLink> pageLinks = crit.add(Restrictions.in("page.id", pageIds_long)).list();
        log.debug("listed objects");
        for (PageLink pl : pageLinks) {
            allLinks.add(pl.getDestinationPage().getUrl());
        }
        tx.commit();
        session.close();
        return allLinks;
    }

    @Override
    public String[] getPageIdsGreaterThanPageId(String lagePageReceived, int limit) {
        return phpDb.getPageIdsGreaterThanPageId(lagePageReceived, limit);
    }

    @Override
    public void updatePageRanks(HashMap<Long, Double> pageRanks) {
        phpDb.updatePageRanks(pageRanks);
    }

    @Override
    public long getPageIdFromUrl(String url) {
        log.debug("Getting pageIdFromUrl '"+url+"'");
        return getPage(url).getId();
    }

    @Override
    public void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults) {
        phpDb.setVerificationStatusForUrls(urlVerificationResults);
    }

    @Override
    public List<String> getUnverifiedPagesForVerification() {
        return phpDb.getUnverifiedPagesForVerification();
    }

    @Override
    public void save(Object entity) {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(entity);
        tx.commit();
        session.close();
    }


}
