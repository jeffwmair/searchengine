package jwm.ir.robotverifier;

import java.util.concurrent.*;

import jwm.ir.crawler.ResourceFetcher;
import jwm.ir.utils.Clock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-21.
 */
public class UrlVerifierMessageReceiverTest {

	private ResourceFetcher fetcher;
	private RobotUserAgentCache cache;
	private Clock clock;
	private Executor executor;

	@Before
	public void setup() {
		this.fetcher = new ResourceFetcher();
		this.clock = mock(Clock.class);
		this.cache = new RobotUserAgentCache(clock);
		this.executor = mock(Executor.class);
	}

	@Test
	public void urlIsVerifiedAndComesThroughToSuccessOutput() {
		BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
		BlockingQueue<String> outputQueueSuccess = new LinkedBlockingQueue<>();
		UrlVerifierFactory factory = new UrlVerifierFactory(fetcher, cache, outputQueueSuccess);
		UrlVerifierMessageReceiver sut = new UrlVerifierMessageReceiver(executor, inputQueue, factory);

	}



}
