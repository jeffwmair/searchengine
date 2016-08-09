package jwm.ir.domain.persistence;

import jwm.ir.domain.Page;
import jwm.ir.utils.AssertUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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

    private Object getByUrl(String url) {
        Object obj = session
                .createCriteria(Page.class)
                .add(Restrictions.eq("url", url))
                .uniqueResult();
        return obj;
    }

}
