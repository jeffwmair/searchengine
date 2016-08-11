package jwm.ir.crawler;

import jwm.ir.service.Service;
import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Db;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Responsible for getting urls-to-crawl from the DB and providing for
 * someone else.
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeed {

    private static final Logger log = LogManager.getLogger(UrlFeed.class);
    private final Service service;
    private final BlockingQueue<String> output;
    public UrlFeed(Service service, BlockingQueue<String> output) {
        AssertUtils.notNull(service, "Must provide a non-null service");
        AssertUtils.notNull(output, "Must provide a non-null output queue");
        this.service = service;
        this.output = output;
    }

    /**
     * Get all the urls from the DB and put them on a queue for someone else.
     */
    public void process() {


        log.debug("Fetching urls from the db.  Putting onto the queue.");
        List<String> urls = service.getUrlsToCrawl();
        log.debug("Fetched "+urls.size()+" urls from the db.  Putting onto the queue.");
        for(String url : urls) {
            try {
                output.put(url);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
    }
}
