package jwm.ir.domain.persistence;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.utils.AssertUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;

/**
 * Created by Jeff on 2016-08-01.
 */
public class PageRepositoryImpl implements PageRepository {

    private final Session session;

    public PageRepositoryImpl(Session session) {
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
    public Page create(String url, DomainRepository domainRepository) {
        Page p = Page.create(url, domainRepository);
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

    private Object getByUrl(String url) {
        Object obj = session
                .createCriteria(Page.class)
                .add(Restrictions.eq("url", url))
                .uniqueResult();
        return obj;
    }

}
