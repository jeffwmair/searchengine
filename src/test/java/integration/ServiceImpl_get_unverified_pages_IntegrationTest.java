package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-08-13.
 */
public class ServiceImpl_get_unverified_pages_IntegrationTest extends DbTestBase {

    @Test
    public void get_unverified_pages() {

        // create some unverified pages
        Domain d = createAndSaveDomain("google.com");
        createAndSavePage("google.com/a", d);
        createAndSavePage("google.com/b", d);

        ServiceImpl sut = new ServiceImpl(sessionFactory, new DaoFactory());
        List<String> unverifiedPageUrls = sut.getUnverifiedPageUrls();

        Assert.assertEquals("should be 2 urls", 2, unverifiedPageUrls.size());
        Assert.assertTrue(unverifiedPageUrls.contains("google.com/a"));
        Assert.assertTrue(unverifiedPageUrls.contains("google.com/b"));

    }
}
