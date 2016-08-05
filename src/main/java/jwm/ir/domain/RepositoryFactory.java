package jwm.ir.domain;

import org.hibernate.Session;

/**
 * Created by Jeff on 2016-08-04.
 */
public class RepositoryFactory {
    public PageRepository createPageRepository(Session session) {
        return new PageRepositoryImpl(session);
    }

    public DomainRepository createDomainRepository(Session session) {
        return new DomainRepositoryImpl(session);
    }

    public PageLinkRepository createPageLinkRepository(Session session) {
        return new PageLinkRepositoryImpl(session);
    }
}
