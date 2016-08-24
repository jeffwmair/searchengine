package com.jwm.ir.index.crawler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeedRunner {

    private final ScheduledExecutorService executorService;
    private final UrlFeed feed;

    public UrlFeedRunner(ScheduledExecutorService executorService,
                         UrlFeed feed) {
        this.executorService = executorService;
        this.feed = feed;
    }

    public void start() {

        long initialDelay = 0;
        long delay = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                feed.process();
            }
        }, initialDelay, delay, timeUnit);
    }
}
