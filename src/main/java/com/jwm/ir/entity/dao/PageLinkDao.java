package com.jwm.ir.entity.dao;

import com.jwm.ir.entity.Page;
import com.jwm.ir.entity.PageLink;

/**
 * Created by Jeff on 2016-08-02.
 */
public interface PageLinkDao {
    PageLink create(Page page, Page referant);
}
