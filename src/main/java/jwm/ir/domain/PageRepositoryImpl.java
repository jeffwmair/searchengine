package jwm.ir.domain;

import jwm.ir.service.Operation;
import jwm.ir.service.UnitOfWork;
import jwm.ir.utils.AssertUtils;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Jeff on 2016-08-01.
 */
public class PageRepositoryImpl implements PageRepository {

    private final UnitOfWork unitOfWork;

    public PageRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
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
    public Page create(String url, DomainRepository domainRepository) {
        Page p = Page.create(url, domainRepository);
        unitOfWork.add(p, Operation.OperationType.Save);
        return p;
    }

    private Object getByUrl(String url) {
        Object obj = unitOfWork.getSession()
                .createCriteria(Page.class)
                .add(Restrictions.eq("url", url))
                .uniqueResult();
        return obj;
    }

}