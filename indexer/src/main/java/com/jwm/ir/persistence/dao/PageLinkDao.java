package com.jwm.ir.persistence.dao;

import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.PageLink;

/**
 * Created by Jeff on 2016-08-02.
 */
public interface PageLinkDao {
    PageLink create(Page page, Page referant);
}
