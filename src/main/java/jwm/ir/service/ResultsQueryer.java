package jwm.ir.service;

import jwm.ir.entity.Page;

import java.util.List;

/**
 * Created by Jeff on 2016-08-14.
 */
public class ResultsQueryer {

    private final List<Page> pages;
    public ResultsQueryer(List<Page> pages) {
        this.pages = pages;
    }
    public List<Page> queryPages(String query) {
       throw new RuntimeException("not implemented");
    }
}
