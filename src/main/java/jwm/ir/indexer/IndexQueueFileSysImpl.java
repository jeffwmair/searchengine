package jwm.ir.indexer;

/**
 * File-system implementation of IndexQueue
 * Created by Jeff on 2016-07-17.
 */
public class IndexQueueFileSysImpl implements IndexQueue {


    private final int workerId;
    private final IndexFileSys indexFileSys;
    public IndexQueueFileSysImpl(int workerId, IndexFileSys indexFileSys) {
        if (indexFileSys == null) throw new IllegalArgumentException("Must provide indexFileSys");
        this.workerId = workerId;
        this.indexFileSys = indexFileSys;
    }

    @Override
    public void put(ParsedWebPage page) {
        indexFileSys.writeToDisk(workerId, page);
    }

    @Override
    public ParsedWebPage pop() {
        return indexFileSys.readFromDisk(workerId);
    }

    @Override
    public int getSize() {
        return indexFileSys.countFiles(workerId);
    }
}
