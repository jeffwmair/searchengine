package jwm.ir.crawler;

import jwm.ir.message.WebResource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Jeff on 2016-07-21.
 */
public class ResourceFetcherWithQueueOut implements Runnable {

    final private static Logger log = LogManager.getLogger(ResourceFetcherWithQueueOut.class);
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
