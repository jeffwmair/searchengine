package integration;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.Page;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbImpl_SaveNewPage_IntegrationTest extends DbTestBase {

    @Test
    public void test_save_new_page() {

        DomainRepository domainRepository = mock(DomainRepository.class);
        Page page = Page.create("http://localhost/searchengine_test/page1.html", domainRepository);
        db.save(page.getDomain());
        db.save(page);

    }

}
