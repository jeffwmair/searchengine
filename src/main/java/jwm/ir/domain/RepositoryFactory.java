package jwm.ir.domain;

import jwm.ir.domain.persistence.*;
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

    public TermDao createTermDao(Session session) {
        return new TermDaoImpl(session);
    }

    public PageTermDao createPageTermDao(Session session) {
        return new PageTermDaoImpl(createTermDao(session), createPageRepository(session), session);
    }

    public ExtensionDao createExtensionDao(Session session) {
        return new ExtensionDaoImpl(session);
    }
}
