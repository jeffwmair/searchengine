package jwm.ir.domain;

/**
 * Created by Jeff on 2016-08-02.
 */
public interface PageLinkRepository {
    PageLink create(Page page, Page referant);
}
