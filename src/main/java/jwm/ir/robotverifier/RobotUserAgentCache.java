package jwm.ir.robotverifier;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Cleanable;
import jwm.ir.utils.Clock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotUserAgentCache implements Cleanable {

    // 72 hrs
    private final long MAX_TIME_IN_CACHE_MILLISECONDS = 259200000;
    private final Map<String, RobotUserAgentDateWrapper> cache;
    private final Clock clock;

    public RobotUserAgentCache(Clock clock) {
        AssertUtils.notNull(clock, "provide a clock");
        this.clock = clock;
        cache = new HashMap<>();

    }

    /**
     * Get an item from the cache
     * @param allegedDomain
     * @return
     */
    public synchronized RobotUserAgent get(String allegedDomain) {
        if (!cache.containsKey(allegedDomain)) {
            return new RobotUserAgentNone();
        }
        return cache.get(allegedDomain).getAgent();
    }

    /**
     * Add an item to the cache
     * @param url
     * @param item
     */
    public synchronized void add(String url, RobotUserAgent item) {
        AssertUtils.failState(!UrlUtils.isDomain(url), "Must provide a domain, but was provided with:"+url);
        cache.put(url, new RobotUserAgentDateWrapper(clock.getTime(), item));
    }

    @Override
    public synchronized void cleanup() {

        Iterator<String> it = cache.keySet().iterator();
        while (it.hasNext()) {
            String domain = it.next();
            RobotUserAgentDateWrapper agent = cache.get(domain);
            long timePassed = clock.getTime() - agent.getTime();
            if (timePassed >= MAX_TIME_IN_CACHE_MILLISECONDS) {
                it.remove();
            }
        }
    }
}
