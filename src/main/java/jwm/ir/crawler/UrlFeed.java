package jwm.ir.crawler;

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
    private final Db db;
    private final BlockingQueue<String> output;
    public UrlFeed(Db db, BlockingQueue<String> output) {
        AssertUtils.notNull(db, "Must provide a non-null db");
        AssertUtils.notNull(output, "Must provide a non-null output queue");
        this.db = db;
        this.output = output;
    }

    /**
     * Get all the urls from the DB and put them on a queue for someone else.
     */
    public void process() {
        List<String> urls = db.getUrls();
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
