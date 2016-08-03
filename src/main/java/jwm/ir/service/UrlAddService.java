package jwm.ir.service;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by Jeff on 2016-08-01.
 */
public class UrlAddService {

    private static final Logger log = LogManager.getLogger(UrlAddService.class);

    public void addUrlForCrawling(String url, String parentUrl,
                                  PageRepository pageRepository,
                                  DomainRepository domainRepository,
                                  PageLinkRepository pageLinkRepository) {

        if (pageRepository.pageExists(url)) {
			throw new IllegalStateException("Page with url '"+url+"' is already saved in the database");
        }

        Page page = pageRepository.create(url, domainRepository);
        Domain pageDomain;
        final String pageDomainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        if (domainRepository.domainExists(pageDomainName)) {
            pageDomain = domainRepository.getDomain(pageDomainName);
        }
        else {
            pageDomain = domainRepository.create(pageDomainName);
        }

        page.setDomain(pageDomain);

        // we assume that the parent page must have already been indexed; how else can it be the parent?
        Page referantPage = pageRepository.getPage(parentUrl);

        pageLinkRepository.create(page, referantPage);

    }
}
