package jwm.ir.domain;

import jwm.ir.service.Operation;
import jwm.ir.service.UnitOfWork;

/**
 * Created by Jeff on 2016-08-02.
 */
public class PageLinkRepositoryImpl implements PageLinkRepository {

    private final UnitOfWork unitOfWork;
    public PageLinkRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public PageLink create(Page page, Page referant) {
        PageLink pl = new PageLink();
        pl.setPage(referant);
        pl.setDestinationPage(page);
        unitOfWork.add(pl, Operation.OperationType.Save);
        return pl;
    }
}
