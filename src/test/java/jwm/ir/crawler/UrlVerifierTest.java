package jwm.ir.crawler;

import jwm.ir.robotverifier.RobotUserAgentCache;
import jwm.ir.robotverifier.UrlVerifier;
import jwm.ir.utils.Clock;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-20.
 */
public class UrlVerifierTest {

    @Test
    public void verifyIndexibleUrlTest() {

        ResourceFetcher fetcher = mock(ResourceFetcher.class);
        Clock clock = mock(Clock.class);
        RobotUserAgentCache robotCache = new RobotUserAgentCache(clock);
        UrlVerifier sut = new UrlVerifier(fetcher, robotCache);
        String anyurl = "http://foobar.com";

        /*
        sut.verifyCanIndex()
        */


    }
}
