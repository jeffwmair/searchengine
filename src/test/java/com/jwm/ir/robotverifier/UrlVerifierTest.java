package com.jwm.ir.robotverifier;

import com.jwm.ir.crawler.ResourceFetcher;
import com.jwm.ir.message.WebResourcePageImpl;
import com.jwm.ir.utils.Clock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jeff on 2016-07-20.
 */
public class UrlVerifierTest {

    private UrlVerifierFactory verifierFactory;
    private ResourceFetcher fetcher;
    private BlockingQueue<String> outputQueue;

    @Before
    public void setup () {
        fetcher = mock(ResourceFetcher.class);
        Clock clock = mock(Clock.class);
        RobotUserAgentCache cache = new RobotUserAgentCache(clock);
        outputQueue = new LinkedBlockingQueue<>();
        verifierFactory = new UrlVerifierFactory(fetcher, cache, outputQueue);
    }

    @Test
    public void verifyDeniedUrlTest() {

		String url = "http://foobar.com/searchengine_test/page1.html";
        WebResourcePageImpl resource = new WebResourcePageImpl("foobar.com/robots.txt", "User-agent:*\nDisallow:/");
        when(fetcher.getWebResource("foobar.com/robots.txt")).thenReturn(resource);
        UrlVerifier sut = verifierFactory.newUrlVerifier(url);
		sut.run();

		Assert.assertTrue(!outputQueue.contains(url));

    }

    @Test
    public void verifyIndexibleUrlTest() {

		String url = "http://foobar.com/searchengine_test/page1.html";
        WebResourcePageImpl resource = new WebResourcePageImpl("foobar.com/robots.txt", "");
        when(fetcher.getWebResource("foobar.com/robots.txt")).thenReturn(resource);
        UrlVerifier sut = verifierFactory.newUrlVerifier(url);
		sut.run();

		Assert.assertTrue(outputQueue.contains(url));

    }
}
