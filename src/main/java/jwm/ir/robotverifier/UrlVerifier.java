package jwm.ir.robotverifier;

import jwm.ir.crawler.ResourceFetcher;
import jwm.ir.crawler.UrlUtils;
import jwm.ir.message.WebResource;
import jwm.ir.utils.AssertUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for verifying if url can be fetched and indexed.
 * Created by Jeff on 2016-07-20.
 */
public class UrlVerifier {

    private final ResourceFetcher fetcher;
    private final RobotUserAgentCache cache;

    public UrlVerifier(ResourceFetcher fetcher, RobotUserAgentCache cache) {
        AssertUtils.notNull(fetcher, "Must provide non-null fetcher");
        AssertUtils.notNull(cache, "Must provide non-null cache");
        this.fetcher = fetcher;
        this.cache = cache;
    }

    boolean verifyCanIndex(String url) {

        String robotsTxtUrl = UrlUtils.getRobotsTxtUrl(url);
        WebResource robots_txt = fetcher.fetch(robotsTxtUrl);


        throw new RuntimeException("not impl");

    }


}
