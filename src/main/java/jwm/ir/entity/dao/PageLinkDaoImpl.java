package jwm.ir.entity.dao;

import jwm.ir.entity.Page;
import jwm.ir.entity.PageLink;
import org.hibernate.Session;

/**
 * Created by Jeff on 2016-08-02.
 */
public class PageLinkDaoImpl implements PageLinkDao {

    private final Session session;
    public PageLinkDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public PageLink create(Page page, Page referant) {
        PageLink pl = new PageLink();
        pl.setPage(referant);
        pl.setDestinationPage(page);
        session.save(pl);
        return pl;
    }
}
