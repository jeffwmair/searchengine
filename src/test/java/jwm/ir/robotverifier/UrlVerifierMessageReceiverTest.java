package jwm.ir.robotverifier;

import java.util.concurrent.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-21.
 */
public class UrlVerifierMessageReceiverTest {

	@Test
	public void urlIsVerifiedAndComesThroughToSuccessOutput() {
		BlockingQueue<String> inputQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<String> outputQueueSuccess = new LinkedBlockingQueue<String>();
		BlockingQueue<String> outputQueueDiscard = new LinkedBlockingQueue<String>();
		UrlVerifierMessageReceiver sut = new UrlVerifierMessageReceiver(inputQueue, outputQueueSuccess, outputQueueDiscard);

	}

	@Test
	public void urlIsDeniedAndComesThroughToDiscardOutput() {
		BlockingQueue<String> inputQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<String> outputQueueSuccess = new LinkedBlockingQueue<String>();
		BlockingQueue<String> outputQueueDiscard = new LinkedBlockingQueue<String>();
		UrlVerifierMessageReceiver sut = new UrlVerifierMessageReceiver(inputQueue, outputQueueSuccess, outputQueueDiscard);

	}


}
