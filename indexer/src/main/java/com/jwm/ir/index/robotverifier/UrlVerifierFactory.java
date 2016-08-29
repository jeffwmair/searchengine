package com.jwm.ir.index.robotverifier;

import com.jwm.ir.index.crawler.ResourceFetcher;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Jeff on 2016-07-21.
 */
public class UrlVerifierFactory {

    private final ResourceFetcher fetcher;
    private final RobotUserAgentCache cache;
    private final BlockingQueue<String> output;
    public UrlVerifierFactory(ResourceFetcher fetcher, RobotUserAgentCache cache, BlockingQueue<String> output) {
        this.fetcher = fetcher;
        this.cache = cache;
        this.output = output;
    }
    public UrlVerifier newUrlVerifier(String url) {
        return new UrlVerifier(url, fetcher, cache, output);
    }
}
