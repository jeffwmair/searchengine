package com.jwm.ir.index.service;

import com.jwm.ir.index.crawler.UrlUtils;
import com.jwm.ir.entity.Domain;
import com.jwm.ir.entity.Page;
import com.jwm.ir.entity.dao.*;
import com.jwm.ir.index.StemmerWrapper;
import com.jwm.ir.utils.AssertUtils;
import com.jwm.ir.entity.SummaryData;
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

        DomainDao domainDao = daoFactory.createDomainRepository(session);
        PageDao pageDao = daoFactory.createPageRepository(session);
        PageLinkDao pageLinkDao = daoFactory.createPageLinkRepository(session);

        // only run if the page doesn't exist
        if (pageDao.pageExists(url)) {
            log.warn("Page with url '"+url+"' already exists in the database, so not adding");
            tx.rollback();
            return;
        }

        Page page = pageDao.create(url, domainDao);
        Domain pageDomain;
        final String pageDomainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        if (domainDao.domainExists(pageDomainName)) {
            log.debug("Domain exists:"+pageDomainName);
            pageDomain = domainDao.getDomain(pageDomainName);
        }
        else {
            log.debug("Domain does not exist, so creating:"+pageDomainName);
            pageDomain = domainDao.create(pageDomainName);
        }

        page.setDomain(pageDomain);

        // we assume that the parent page must have already been indexed; how else can it be the parent?
        Page referantPage = pageDao.getPage(parentUrl);

        pageLinkDao.create(page, referantPage);

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
        PageDao pageDao = daoFactory.createPageRepository(session);

        pageDao.setPageCrawlResult(url, pageTitle, pageDesc, result);

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

        PageDao pageDao = daoFactory.createPageRepository(session);
        log.info("Beginning to update pageranks for " + pageRanks.size() + " pages");
        pageDao.updatePageRanks(pageRanks);
        log.info("Completed updating pageranks");

        tx.commit();
        session.close();
    }

    @Override
    public void updateSummaries() {

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();


        // fetch the total number of page-ranked pages
        int rankedPageCount = session
                .createCriteria(Page.class)
                .add(Restrictions.isNotNull("pageRank"))
                .add(Restrictions.eq("verified", 1)).list().size();

        Object record = session.createCriteria(SummaryData.class).add(Restrictions.eq("item", SummaryData.ItemIndexedPageCount)).uniqueResult();
        SummaryData summaryData;
        if (record != null) {
            summaryData = (SummaryData)record;
        }
        else {
            summaryData = new SummaryData();
        }

        summaryData.update(SummaryData.ItemIndexedPageCount, rankedPageCount);
        session.saveOrUpdate(summaryData);

        tx.commit();
        session.close();

    }

    @Override
    public void setUrlsAsVerified(List<String> verifiedUrls) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        for(String url : verifiedUrls) {
            Page p = (Page)getPageObject(url, session);
            p.setIsVerified();
            log.debug("setting page as verified:"+p);
        }
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
            String stemmedTermValue = StemmerWrapper.stem(termValue);
            pageTermDao.create(pageId, stemmedTermValue, tf);

        }

        tx.commit();
        session.close();
    }

    @Override
    public List<Page> getPages(FilterVerified filterVerified) {
        Session session = sessionFactory.openSession();
        Criteria crit = session.createCriteria(Page.class);
        if (filterVerified == FilterVerified.VerifiedOnly) {
            crit.add(Restrictions.eq("verified", Page.IsVerified));
        }
        else if (filterVerified == FilterVerified.UnverifiedOnly) {
            crit.add(Restrictions.eq("verified", Page.UnVerified));
        }
        List<Page> pages = crit.list();
        session.close();
        return pages;
    }

    @Override
    public List<Page> getScoredPagesFromQuery(String query) {
        throw new  RuntimeException("not impl");
    }

    @Override
    public boolean pageExists(String url) {
        Session session = sessionFactory.openSession();
        boolean exists = getPageObject(url, session) != null;
        session.close();
        return exists;
    }

    private Object getPageObject(String url, Session session) {
        return session.createCriteria(Page.class).add(Restrictions.eq("url", url)).uniqueResult();
    }

    @Override
    public Page getPage(String url) {
        if (!pageExists(url)) {
            throw new RuntimeException("Page does not exist with url '"+url+"'");
        }

        Session session = sessionFactory.openSession();
        Page page = (Page)getPageObject(url, session);
        session.close();
        return page;
    }
	/*

	Might need this for reference for a while.

	private final int MAX_URLS_FRO_1_DOMAIN_TO_CRAWL = 10;

	@Override
	public List<String> popUrls() {

		Map json = HttpUtils.httpPost(_webServiceHost,
				"crawlerid",
				"1",
				"GetPagesToCrawl.php",
				true);


		HashMap<String, Integer> domainPageCounter = new HashMap<>();
		List<String> retVal = new ArrayList<>();

		if (json == null) return retVal;

		int pageCount = json.size();
		for(int i = 1; i <= pageCount; i++) {
			ArrayList<HashMap<String,String>> list = (ArrayList<HashMap<String, String>>) json.get("root");
			for(HashMap<String,String> item : list) {

				String domain = item.get("domain");
				String pageUrl = item.get("url");

				Integer domainPageCount = domainPageCounter.get(domain);
				if (domainPageCount == null) {
					domainPageCount = 1;
				}
				else
				{
					domainPageCount++;
				}

				domainPageCounter.put(domain, domainPageCount);

				if (domainPageCount < MAX_URLS_FRO_1_DOMAIN_TO_CRAWL) {
					retVal.add(pageUrl);
				}
			}
		}

		return retVal;
	}


	*/

//	<?php
//	require_once './utils.php';
//	$crawlerid = getRequestData('crawlerid');
//
//	 variables
//	$limit = 50;
//	$minDomainCrawlRateMinutes = 120;
//	$minDomainCrawlRateMinutes = 0;
//	$failedPagePenaltyInMin = 240;
//	$minPageCrawlRateMinutes = 60 * 24 * 14;
//
//	$conn = connect();

	// put the pages into the temp table
	//$sql = "select d.domain, p.url, d.status, d.last_crawl, p.last_crawl
//			from domains d
//				join pages p on d.domainId = p.domainId
//			where d.status >= 0 and p.verified = 1 and d.crawlerId = $crawlerid and
//			(
//			(d.status = 1)
//			or
//			(d.last_crawl is null)
//			or
//	       	(
//			(hour(timediff(now(),d.last_crawl))*60 + minute(timediff(now(),d.last_crawl))) > $minDomainCrawlRateMinutes)
//	        or
//	        ( ( 60*(hour(timediff(now(),p.last_crawl))) + (minute(timediff(now(),p.last_crawl))) ) > ($minPageCrawlRateMinutes + p.fail_count * $failedPagePenaltyInMin) ) )
//			order by
//				(case when d.last_crawl is null then '0000-00-00' else d.last_crawl end) asc
//				, d.last_crawl asc
//				, (case when p.last_crawl is null then '0000-00-00' else p.last_crawl end) asc
//				, d.total_crawls desc
//		limit $limit;";
//	$res = execSql($conn, $sql);
//
//	echo convertSqlRowsToJson($res);
//
//?>
}
