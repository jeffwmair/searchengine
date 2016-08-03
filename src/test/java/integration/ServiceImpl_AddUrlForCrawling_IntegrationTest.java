package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.service.ServiceImpl;
import jwm.ir.service.UrlAddService;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl_AddUrlForCrawling_IntegrationTest {
    private ServiceImpl sut;
    private Db db;

    @Test
    public void test_add_url_to_be_crawled() {

        // arrange
        String parentUrl = "www.google.com/a";
        String url = "www.google.com/b";
        Domain d = Domain.createFromUrl("google.com");
        Page parent = Page.create(d, "www.google.com/a");
        // the parent page needs to exist before-hand
        db.save(d);
        db.save(parent);

        // act
        sut.addUrlForCrawling(url, parentUrl);

        // assert
        List<String> parentUrls = new ArrayList<>();
        String parentUrlId = Long.toString(db.getPage(parentUrl).getId());
        parentUrls.add(parentUrlId);
        Page page = db.getPage(url);
        List<String> links = db.getPageLinks(parentUrls);
        Assert.assertEquals(url, page.getUrl());
        Assert.assertEquals("Should be 1 outlink", 1, links.size());
        Assert.assertEquals("Outlink should be 'www.google.com/b'", "www.google.com/b", links.get(0));


    }

    @Before
    public void setup() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
        UrlAddService urlAddService = new UrlAddService();
        sut = new ServiceImpl(sessionFactory, urlAddService);
    }
}
