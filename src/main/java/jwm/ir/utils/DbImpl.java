package jwm.ir.utils;

import jwm.ir.domain.Page;
import jwm.ir.domain.ValidExtension;
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
        return (Page)criteria.uniqueResult();
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
    public void addDocumentTerms(String json, int pageId) {
        phpDb.addDocumentTerms(json, pageId);
    }

    @Override
    public ArrayList<String> getPageLinks(ArrayList<String> pageIds) {
        return phpDb.getPageLinks(pageIds);
    }

    @Override
    public String[] getPageIdsGreaterThanPageId(String lagePageReceived, int limit) {
        return phpDb.getPageIdsGreaterThanPageId(lagePageReceived, limit);
    }

    @Override
    public void updatePageRanks(HashMap<Integer, Double> pageRanks) {
        phpDb.updatePageRanks(pageRanks);
    }

    @Override
    public int getPageIdFromUrl(String url) {
        return phpDb.getPageIdFromUrl(url);
    }

    @Override
    public void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults) {
        phpDb.setVerificationStatusForUrls(urlVerificationResults);
    }

    @Override
    public void addCrawlResult(String url, String pageTitle, String pageDesc, Date crawlTime, boolean successful) {
        phpDb.addCrawlResult(url, pageTitle, pageDesc, crawlTime, successful);
    }

    @Override
    public void addNewUrls(String containingPage, ArrayList<String> urls) throws Exception {
        phpDb.addNewUrls(containingPage, urls);
    }

    @Override
    public ArrayList<String> getUnverifiedPagesForVerification() {
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

    @Override
    public List<String> getValidDomainExtensions() {

        List<String> domains;
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        domains = session.createCriteria(ValidExtension.class).list();
        tx.commit();
        session.close();
        return domains;
    }
}
