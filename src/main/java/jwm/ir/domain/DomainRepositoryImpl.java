package jwm.ir.domain;

import jwm.ir.utils.AssertUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl implements DomainRepository {

    private final SessionFactory sessionFactory;

    public DomainRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean domainExists(String domain) {
        Session session = sessionFactory.openSession();
        Object domainObject = session
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domain))
                .uniqueResult();
        session.close();

        return domainObject != null;
    }

    @Override
    public Domain getDomain(String domain) {
        Session session = sessionFactory.openSession();
        Object domainObject = session
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domain))
                .uniqueResult();
        session.close();

        AssertUtils.notNull(domainObject, "Could not find domain object with name '"+domain+"'");
        return (Domain)domainObject;
    }
}
