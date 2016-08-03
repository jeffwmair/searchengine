package jwm.ir.domain;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

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