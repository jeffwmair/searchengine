package jwm.ir.domain.dao;

import jwm.ir.domain.Domain;

/**
 * Created by Jeff on 2016-07-31.
 */
public interface DomainDao {
    boolean domainExists(String domain);
    Domain getDomain(String domain);
    Domain create(String pageDomainName);
}
