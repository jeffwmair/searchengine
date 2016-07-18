package jwm.ir.indexer;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * File-system implementation of IndexQueue
 * Created by Jeff on 2016-07-17.
 */
class IndexQueueFileSysImpl implements IndexQueue {

    final private static Logger log = LogManager.getLogger(IndexQueueFileSysImpl.class);

    private final IndexFileSys indexFileSys;
    IndexQueueFileSysImpl(IndexFileSys indexFileSys) {
        if (indexFileSys == null) throw new IllegalArgumentException("Must provide indexFileSys");
        this.indexFileSys = indexFileSys;
    }

    @Override
    public synchronized void put(int workerId, ParsedWebPage page) {
        log.debug("Putting page into the queue:"+page.getUrl()+"; worker:"+workerId);
        indexFileSys.writeToDisk(workerId, page);
    }

    @Override
    public synchronized ParsedWebPage pop(int workerId) {
        ParsedWebPage page = indexFileSys.readFromDisk(workerId);
        log.debug("Popping page from the queue:"+page.getUrl()+"; worker:"+workerId);
        return page;
    }

    @Override
    public synchronized int getSize(int workerId) {
        return indexFileSys.countFiles(workerId);
    }
}
