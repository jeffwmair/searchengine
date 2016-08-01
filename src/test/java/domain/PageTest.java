package domain;

import jwm.ir.domain.Domain;
import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.Page;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jeff on 2016-07-27.
 */
public class PageTest {

    @Test
    public void test_create_page_from_url_with_domain() {
        String url = "http://www.google.com/foo";
        DomainRepository domainRepo = mock(DomainRepository.class);
        Page page = Page.create(url, domainRepo);
        Assert.assertEquals(url, page.getUrl());
    }
}
