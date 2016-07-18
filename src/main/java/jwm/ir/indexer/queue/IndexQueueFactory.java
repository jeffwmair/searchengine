package jwm.ir.indexer.queue;

/**
 * Created by Jeff on 2016-07-17.
 */
public class IndexQueueFactory {
    private static IndexQueue instance;
    public static IndexQueue getQueue(String directory) {
        if (instance == null) {
            IndexFileSys fs = new IndexFileSysImpl(directory);
            instance = new IndexQueueFileSysImpl(fs);
        }
        return instance;
    }
}
