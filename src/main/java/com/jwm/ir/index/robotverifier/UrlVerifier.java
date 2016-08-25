package com.jwm.ir.index.robotverifier;

import com.jwm.ir.index.crawler.ResourceFetcher;
import com.jwm.ir.index.crawler.UrlUtils;
import com.jwm.ir.index.resource.WebResource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.concurrent.BlockingQueue;

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

    UrlVerifier(String url,
					   ResourceFetcher fetcher,
					   RobotUserAgentCache cache,
					   BlockingQueue<String> outputQueue) {
		Assert.hasLength(url, "Must provide a url");
        Assert.notNull(fetcher, "Must provide non-null fetcher");
        Assert.notNull(cache, "Must provide non-null cache");
        Assert.notNull(outputQueue, "Must provide non-null outputQueue");
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
        	WebResource robots_txt = fetcher.getWebResource(UrlUtils.getRobotsTxtUrl(url));
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
