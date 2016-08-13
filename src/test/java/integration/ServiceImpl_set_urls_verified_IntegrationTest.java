package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-08-13.
 */
public class ServiceImpl_set_urls_verified_IntegrationTest extends DbTestBase {

    @Test
    public void set_one_url_verified() {

        // create some unverified pages
        Domain d = createAndSaveDomain("google.com");
        createAndSavePage("google.com/a", d);
        createAndSavePage("google.com/b", d);

        ServiceImpl service = new ServiceImpl(sessionFactory, new DaoFactory());
        List<String> urlsToSetVerified = new ArrayList<>();
        urlsToSetVerified.add("google.com/a");
        service.setUrlsAsVerified(urlsToSetVerified);

        Assert.assertTrue("this page should have been marked verified", fetchPageFromDb("google.com/a").getIsVerified());
        Assert.assertFalse("this page should not have been marked verified", fetchPageFromDb("google.com/b").getIsVerified());
    }
}
