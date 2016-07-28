package jwm.ir.utils;

import jwm.ir.domain.Page;

import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public interface Db {
    List<String> popUrls();
    List<String> getValidDomainExtensions();
    void save(Object entity);
    Page getPage(String url);
}
