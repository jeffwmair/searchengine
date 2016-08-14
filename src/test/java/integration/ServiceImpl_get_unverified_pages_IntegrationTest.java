package integration;

import jwm.ir.entity.Domain;
import jwm.ir.entity.Page;
import jwm.ir.entity.dao.DaoFactory;
import jwm.ir.service.Service;
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
        List<Page> unverifiedPages = sut.getPages(Service.FilterVerified.UnverifiedOnly);

        Assert.assertEquals("should be 2 urls", 2, unverifiedPages.size());

    }
}
