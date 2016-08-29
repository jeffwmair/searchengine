package com.jwm.ir.persistence.dao;

import com.jwm.ir.persistence.Domain;

/**
 * Created by Jeff on 2016-07-31.
 */
public interface DomainDao {
    boolean domainExists(String domain);
    Domain getDomain(String domain);
    Domain create(String pageDomainName);
}
