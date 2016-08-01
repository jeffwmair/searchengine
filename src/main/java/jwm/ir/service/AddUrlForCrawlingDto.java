package jwm.ir.service;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;

/**
 * Created by Jeff on 2016-07-31.
 */
public class AddUrlForCrawlingDto {
    private final Page page, parentPage;
    private final PageLink pageLink;
    public AddUrlForCrawlingDto(Page page, Page parentPage, PageLink pageLink) {
        this.page = page;
        this.parentPage = parentPage;
        this.pageLink = pageLink;
    }

    public Page getPage() {
        return page;
    }

    public Page getParentPage() {
        return parentPage;
    }

    public PageLink getPageLink() {
        return pageLink;
    }
}
