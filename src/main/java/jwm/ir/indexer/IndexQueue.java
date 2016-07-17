package jwm.ir.indexer;

/**
 * Created by Jeff on 2016-07-17.
 */
public interface IndexQueue {

    /**
     * Put the page on the queue for indexing
     * @param page
     */
    void put(ParsedWebPage page);

    /**
     * Pop a page off the queue to be indexed
     * @return
     */
    ParsedWebPage pop();

    /**
     * Get the size of the queue
     * @return
     */
    int getSize();
}
