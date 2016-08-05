package jwm.ir.domain;

import org.hibernate.Session;

/**
 * Created by Jeff on 2016-08-02.
 */
public class PageLinkRepositoryImpl implements PageLinkRepository {

    private final Session session;
    public PageLinkRepositoryImpl(Session session) {
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
