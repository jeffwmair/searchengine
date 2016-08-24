package com.jwm.ir.index.crawler;

import com.jwm.ir.index.message.WebResource;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Jeff on 2016-07-21.
 */
public class ResourceFetcherWithQueueOut implements Runnable {

    private final ResourceFetcher fetcher;
    private final BlockingQueue<WebResource> output;
    private final String url;
    public ResourceFetcherWithQueueOut(String url, BlockingQueue<WebResource> output) {
        this.fetcher = new ResourceFetcher();
        this.output = output;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            output.put(fetcher.getWebResource(url));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
