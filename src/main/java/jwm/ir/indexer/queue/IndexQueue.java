package jwm.ir.indexer.queue;

import jwm.ir.indexer.ParsedWebPage;

/**
 * Created by Jeff on 2016-07-17.
 */
public interface IndexQueue {

    /**
     * Put the page on the queue for indexing
     * @param page
     */
    void put(int workerId, ParsedWebPage page);

    /**
     * Pop a page off the queue to be indexed
     * @return
     */
    ParsedWebPage pop(int workerId);

    /**
     * Get the size of the queue
     * @return
     */
    int getSize(int workerId);
}
