package com.jwm.ir.entity.dao;

import com.jwm.ir.entity.Domain;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainDaoImpl implements DomainDao {

    private final Session session;

    public DomainDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public boolean domainExists(String domain) {
        return getByDomainName(domain) != null;
    }

    @Override
    public Domain getDomain(String domain) {
        Object domainObject = getByDomainName(domain);
        Assert.notNull(domainObject, "Could not find jwm.ir.domain object with name '"+domain+"'");
        return (Domain)domainObject;
    }

    @Override
    public Domain create(String pageDomainName) {
        Domain domain = Domain.createFromUrl(pageDomainName);
        session.save(domain);
        return domain;
    }

    private Object getByDomainName(String domainName) {
         Object domainObject = session
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domainName))
                .uniqueResult();
        return domainObject;
    }
}
