package jwm.ir.domain;

/**
 * Created by Jeff on 2016-07-31.
 */
public interface DomainRepository {
    boolean domainExists(String domain);
    Domain getDomain(String domain);
    Domain create(String pageDomainName);
}
