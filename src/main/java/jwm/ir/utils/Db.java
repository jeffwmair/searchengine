package jwm.ir.utils;

import jwm.ir.domain.Page;

import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public interface Db {
    List<String> popUrls();
    void save(Page page);
    Page getPage(String url);
}
