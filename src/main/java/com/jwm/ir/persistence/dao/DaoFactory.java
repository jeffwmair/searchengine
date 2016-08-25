package com.jwm.ir.persistence.dao;

import org.hibernate.Session;

/**
 * Created by Jeff on 2016-08-04.
 */
public class DaoFactory {

    public PageDao createPageRepository(Session session) {
        return new PageDaoImpl(session);
    }

    public DomainDao createDomainRepository(Session session) {
        return new DomainDaoImpl(session);
    }

    public PageLinkDao createPageLinkRepository(Session session) {
        return new PageLinkDaoImpl(session);
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
