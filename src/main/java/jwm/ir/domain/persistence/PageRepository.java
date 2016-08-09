package jwm.ir.domain.persistence;

import jwm.ir.domain.Page;

/**
 * Created by Jeff on 2016-08-01.
 */
public interface PageRepository {
    boolean pageExists(String url);
    Page getPage(String url);
    Page getPage(long pageId);
    Page create(String url, DomainRepository domainRepository);
}
