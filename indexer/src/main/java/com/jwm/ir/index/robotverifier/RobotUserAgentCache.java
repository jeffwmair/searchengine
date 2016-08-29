package com.jwm.ir.index.robotverifier;

import com.jwm.ir.index.crawler.UrlUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotUserAgentCache implements Cleanable {

    // 72 hrs
    private final long MAX_TIME_IN_CACHE_MILLISECONDS = 259200000;
    private final Map<String, RobotDisallowsDateWrapper> cache;
    private final Clock clock;

    public RobotUserAgentCache(Clock clock) {
        Assert.notNull(clock, "provide a clock");
        this.clock = clock;
        cache = new HashMap<>();

    }

    /**
     * Get an item from the cache
     * @param domain
     * @return
     */
    public synchronized RobotDisallows get(String domain) {
        if (!cache.containsKey(domain)) {
            return new RobotDisallows(new ArrayList<String>());
        }
        return cache.get(domain).getDisallows();
    }

	public synchronized boolean contains(String domain) {
		return cache.containsKey(domain);
	}

    /**
     * Add an item to the cache
     * @param url
     * @param item
     */
    public synchronized void add(String url, RobotDisallows item) {
        Assert.isTrue(UrlUtils.isDomain(url), "Must provide a jwm.ir.domain, but was provided with:"+url);
        cache.put(url, new RobotDisallowsDateWrapper(clock.getTime(), item));
    }

    @Override
    public synchronized void cleanup() {

        Iterator<String> it = cache.keySet().iterator();
        while (it.hasNext()) {
            String domain = it.next();
            RobotDisallowsDateWrapper disallowsWrapper = cache.get(domain);
            long timePassed = clock.getTime() - disallowsWrapper.getTime();
            if (timePassed >= MAX_TIME_IN_CACHE_MILLISECONDS) {
                it.remove();
            }
        }
    }
}
