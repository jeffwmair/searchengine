package jwm.ir.domain;

/**
 * Created by Jeff on 2016-08-01.
 */
public interface PageRepository {
    boolean pageExists(String url);
    Page getPage(String url);
    Page create(String url, DomainRepository domainRepository);
}
