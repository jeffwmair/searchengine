package jwm.ir.domain;

import jwm.ir.utils.AssertUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl implements DomainRepository {

    private final Session session;

    public DomainRepositoryImpl(Session session) {
        this.session = session;
    }

    @Override
    public boolean domainExists(String domain) {
        Object domainObject = session
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domain))
                .uniqueResult();

        return domainObject != null;
    }

    @Override
    public Domain getDomain(String domain) {
        Object domainObject = session
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domain))
                .uniqueResult();

        AssertUtils.notNull(domainObject, "Could not find domain object with name '"+domain+"'");
        return (Domain)domainObject;
    }
}
