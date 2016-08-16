package jwm.ir.service;

import jwm.ir.entity.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-14.
 */
public class ResultsQueryer {

    private final List<Page> indexedPages;
    public ResultsQueryer(List<Page> indexedPages) {
        this.indexedPages = indexedPages;
    }
    public List<Page> queryPages(String query) {

        Map<String, Integer> queryMap = QueryHelper.getMapOfQueryTerms(query);
        throw new RuntimeException("not implemented");
    }
}
