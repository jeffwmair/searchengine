package jwm.ir.robotverifier;

import jwm.ir.crawler.ResourceFetcher;
import jwm.ir.crawler.UrlUtils;
import jwm.ir.message.WebResource;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.*;

/**
 * Responsible for verifying if url can be fetched and indexed.
 * Created by Jeff on 2016-07-20.
 */
public class UrlVerifier implements Runnable {

	final private static Logger log = LogManager.getLogger(UrlVerifier.class);
	private final String url;
    private final ResourceFetcher fetcher;
    private final RobotUserAgentCache cache;
    private final BlockingQueue<String> outputQueue;

    public UrlVerifier(String url, ResourceFetcher fetcher, RobotUserAgentCache cache, BlockingQueue<String> outputQueue) {
		AssertUtils.notEmpty(url, "Must provide a url");
        AssertUtils.notNull(fetcher, "Must provide non-null fetcher");
        AssertUtils.notNull(cache, "Must provide non-null cache");
        AssertUtils.notNull(outputQueue, "Must provide non-null outputQueue");
		this.url = url;
        this.fetcher = fetcher;
        this.cache = cache;
		this.outputQueue = outputQueue;
    }

	@Override
	public void run() {

        String domainUrl = UrlUtils.getDomainFromAbsoluteUrl(url);
		if (!cache.contains(domainUrl)) {
			// fetch it, put in the cache	
        	WebResource robots_txt = fetcher.fetch(UrlUtils.getRobotsTxtUrl(url));
			RobotTxtParser robotsParser = new RobotTxtParser();
			RobotDisallows disallows = robotsParser.getDisallows(robots_txt.getContent());
			cache.add(domainUrl, disallows);
		}

		RobotDisallows disallows = cache.get(domainUrl);
		if (disallows.canCrawl(url)) {
			try {
				outputQueue.put(url);
			} catch (InterruptedException e) {
				throw new RuntimeException("Could not add url '"+url+"' to the queue:"+e.getLocalizedMessage(), e);
			}
		}
		else {
			log.info("Url cannot be indexed because its robots.txt restricts it.  Url:"+url);
		}

	}


}
