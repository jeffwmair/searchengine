package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.domain.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl_AddUrlForCrawling_IntegrationTest extends DbTestBase {

    @Test
    public void test_add_url_to_be_crawled() {

        // arrange
        String parentUrl = "www.google.com/a";
        String url = "www.google.com/b";
        Domain d = Domain.createFromUrl("google.com");
        Page parent = Page.create(d, "www.google.com/a");
        // the parent page needs to exist before-hand
        save(d);
        save(parent);

        // act
        ServiceImpl sut = new ServiceImpl(sessionFactory, new DaoFactory());
        sut.addUrlForCrawling(url, parentUrl);

        // assert
        Page page = fetchPageFromDb(parentUrl);
        PageLink firstPageLink = page.getPageLinks().get(0);
        Assert.assertEquals("Should be 1 outlink", 1, page.getPageLinks().size());
        Assert.assertEquals("Outlink should be 'www.google.com/b'", "www.google.com/b", firstPageLink.getDestinationPage().getUrl());

    }

}
