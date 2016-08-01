package jwm.ir.service;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;

/**
 * Created by Jeff on 2016-07-31.
 */
public class ServiceLogic implements Service {

    private final DomainRepository domainRepository;
    public ServiceLogic(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }
    @Override
    public AddUrlForCrawlingDto addUrlForCrawling(String url, String parentUrl) {

        Page page = Page.create(url, domainRepository);
        Page parentPage = Page.create(parentUrl, domainRepository);
        PageLink pageLink = PageLink.create(parentPage, page);

        return new AddUrlForCrawlingDto(page, parentPage, pageLink);

    }
}
