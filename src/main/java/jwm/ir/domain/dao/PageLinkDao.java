package jwm.ir.domain.dao;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;

/**
 * Created by Jeff on 2016-08-02.
 */
public interface PageLinkDao {
    PageLink create(Page page, Page referant);
}
