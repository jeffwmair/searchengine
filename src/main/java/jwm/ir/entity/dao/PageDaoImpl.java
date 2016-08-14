package jwm.ir.entity.dao;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.entity.Domain;
import jwm.ir.entity.Page;
import jwm.ir.utils.AssertUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Map;

/**
 * Created by Jeff on 2016-08-01.
 */
public class PageDaoImpl implements PageDao {

    private final Session session;

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
        AssertUtils.notNull(obj, "Could not find page object with url '"+url+"'");
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
