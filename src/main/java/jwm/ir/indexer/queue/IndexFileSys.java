package jwm.ir.indexer.queue;

import jwm.ir.indexer.ParsedWebPage;

/**
 * Created by Jeff on 2016-07-17.
 */
interface IndexFileSys {
    void writeToDisk(int workerId, ParsedWebPage parsedWebPage);
    ParsedWebPage readFromDiskAndDelete(int workerId);
    int countFiles(int workerId);
}
