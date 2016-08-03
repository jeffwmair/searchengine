package jwm.ir.domain;

import jwm.ir.service.Operation;
import jwm.ir.service.UnitOfWork;
import jwm.ir.utils.AssertUtils;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DomainRepositoryImpl implements DomainRepository {

    private final UnitOfWork unitOfWork;

    public DomainRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public boolean domainExists(String domain) {
        return getByDomainName(domain) != null;
    }

    @Override
    public Domain getDomain(String domain) {
        Object domainObject = getByDomainName(domain);
        AssertUtils.notNull(domainObject, "Could not find jwm.ir.domain object with name '"+domain+"'");
        return (Domain)domainObject;
    }

    @Override
    public Domain create(String pageDomainName) {
        Domain domain = Domain.createFromUrl(pageDomainName);
        unitOfWork.add(domain, Operation.OperationType.Save);
        return domain;
    }

    private Object getByDomainName(String domainName) {
         Object domainObject = unitOfWork.getSession()
                .createCriteria(Domain.class)
                .add(Restrictions.eq("domain", domainName))
                .uniqueResult();
        return domainObject;
    }
}
