package jwm.ir.robotverifier;

import jwm.ir.crawler.ResourceFetcher;
import jwm.ir.message.WebResourcePageImpl;
import jwm.ir.utils.Clock;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.internal.matchers.Any;

import java.util.concurrent.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jeff on 2016-07-20.
 */
public class UrlVerifierTest {

    @Test
    public void verifyIndexibleUrlTest() {

		String url = "http://foobar.com/searchengine_test/page1.html";
        ResourceFetcher fetcher = mock(ResourceFetcher.class);
        WebResourcePageImpl resource = new WebResourcePageImpl("foobar.com/robots.txt", "");
        when(fetcher.fetch("foobar.com/robots.txt")).thenReturn(resource);
        Clock clock = mock(Clock.class);
        RobotUserAgentCache robotCache = new RobotUserAgentCache(clock);
		BlockingQueue<String> output = new LinkedBlockingQueue<>();

        UrlVerifier sut = new UrlVerifier(url, fetcher, robotCache, output);
		sut.run();

		// the url was verified as indexable, so it should be passed to the output
		Assert.assertTrue(output.contains(url));


    }
}
