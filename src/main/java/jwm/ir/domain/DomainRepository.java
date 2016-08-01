package jwm.ir.domain;

/**
 * Created by Jeff on 2016-07-31.
 */
public interface DomainRepository {
    public boolean domainExists(String domain);
    public Domain getDomain(String domain);
}
