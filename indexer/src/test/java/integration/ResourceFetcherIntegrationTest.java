package integration;

import com.jwm.ir.index.crawler.ResourceFetcher;
import com.jwm.ir.index.resource.WebResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-21.
 */
public class ResourceFetcherIntegrationTest {

    @Test
    public void fetchLocalPageSynchronouslyTest() {

        ResourceFetcher fetcher = new ResourceFetcher();
        WebResource robotsTxt = fetcher.getWebResource("http://localhost/robots.txt");

        Assert.assertTrue("content was:"+robotsTxt.getContent(), robotsTxt.getContent().equals("User-agent:*\nDisallow:/private"));
    }

}
