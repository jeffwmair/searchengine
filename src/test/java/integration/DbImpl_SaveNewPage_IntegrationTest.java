package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbImpl_SaveNewPage_IntegrationTest extends DbTestBase {

    @Test
    public void test_save_new_page() {

        Domain domain = new Domain("localhost/searchengine_test", 1);
        Page page = new Page(domain, "http://localhost/searchengine_test/page1.html");
        db.save(page);

    }

}
