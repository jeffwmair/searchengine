package jwm.ir.robotverifier;

import java.util.concurrent.*;

import jwm.ir.utils.AssertUtils;

/**
 * Responsible for receiving a url message, and passing it
 * further downstream if verification passes.
 * Created by Jeff on 2016-07-21.
 */
public class UrlVerifierMessageReceiver {

	private final BlockingQueue<String> input;
	private final Executor urlVerifierExecutor;
	private final UrlVerifierFactory urlVerifierFactory;

    public UrlVerifierMessageReceiver(Executor urlVerifierExecutor,
									  BlockingQueue<String> input,
									  UrlVerifierFactory urlVerifierFactory ) {

		AssertUtils.notNull(input, "must provide input queue");
		AssertUtils.notNull(urlVerifierExecutor, "must provide executor");

		this.urlVerifierFactory = urlVerifierFactory;
		this.input = input;
		this.urlVerifierExecutor = urlVerifierExecutor;
    }

	/**
	 * Listen on the input queue for a url to verify
	 * @throws InterruptedException
     */
	public void run() throws InterruptedException {
		while (true) {
			String url = input.take();
			urlVerifierExecutor.execute(urlVerifierFactory.newUrlVerifier(url));
		}
	}



}
