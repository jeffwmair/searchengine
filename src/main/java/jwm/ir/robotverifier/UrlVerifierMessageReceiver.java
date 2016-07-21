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
	private final BlockingQueue<String> outputSuccess;
	private final BlockingQueue<String> outputDiscard;
    public UrlVerifierMessageReceiver(BlockingQueue<String> input, 
			BlockingQueue<String> outputSuccess, 
			BlockingQueue<String> outputDiscard) {

		AssertUtils.notNull(input, "must provide input queue");
		AssertUtils.notNull(outputSuccess, "must provide output-success queue");
		AssertUtils.notNull(outputDiscard, "must provide output-discard queue");

		this.input = input;
		this.outputSuccess = outputSuccess;
		this.outputDiscard = outputDiscard;
    }



}
