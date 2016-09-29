package com.jwm.ir.persistence.dao;

import com.jwm.ir.index.crawler.UrlUtils;
import com.jwm.ir.persistence.Domain;
import com.jwm.ir.persistence.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-01.
 */
public class PageDaoImpl implements PageDao {

    private final Session session;
    private static final Logger log = LogManager.getLogger(PageDao.class);

    public PageDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public boolean pageExists(String url) {
        return getByUrl(url) != null;
    }

    @Override
    public Page getPage(String url) {

        Object obj = getByUrl(url);
        Assert.notNull(obj, "Could not find page object with url '"+url+"'");
        return (Page)obj;
    }

    @Override
    public Page getPage(long pageId) {
        return (Page)session.get(Page.class, pageId);
    }

    @Override
    public Page create(String url, DomainDao domainDao) {
        Page p = new Page(url, Page.MakeNewDomain.No);
        String domainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        if (domainDao.domainExists(domainName)) {
            p.setDomain(domainDao.getDomain(domainName));
        }
        else {
            Domain d = new Domain(domainName);
            p.setDomain(d);
        }
        session.save(p);
        return p;
    }

    /**
     * update domain for page, set total_crawls++, set status = 0, set crawlTime
     * set page fail_count += (success)
     * @param url
     * @param pageTitle
     * @param pageDesc
     * @param result
     */
    @Override
    public void setPageCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result) {
        Page p = getPage(url);
        p.getDomain().incrementTotalCrawls();
        p.getDomain().setStatus(0);
        p.getDomain().updateLastCrawl();
        p.updateFromCrawl(pageTitle, pageDesc, result);
        session.update(p.getDomain());
        session.update(p);
    }

    @Override
    public void updatePageRanks(Map<Long, Double> pageRanks) {
        for(Long pageId : pageRanks.keySet()) {
            Double pageRankValue = pageRanks.get(pageId);
            setPageRank(pageId, pageRankValue);
        }
    }

    @Override
    public int getIndexedPageCount() {
        return session.createCriteria(Page.class).add(Restrictions.isNotNull("pageRank")).list().size();
    }

    private void setPageRank(Long pageId, Double pageRankValue) {
        Page p = getPage(pageId);
        p.setPageRank(pageRankValue);
        session.update(p);
    }

    private Object getByUrl(String url) {
        Object obj = session
                .createCriteria(Page.class)
                .add(Restrictions.eq("url", url))
                .uniqueResult();
        return obj;
    }

}
