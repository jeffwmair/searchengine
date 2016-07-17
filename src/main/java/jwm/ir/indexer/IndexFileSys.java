package jwm.ir.indexer;

/**
 * Created by Jeff on 2016-07-17.
 */
public interface IndexFileSys {
    void writeToDisk(int workerId, ParsedWebPage parsedWebPage);
    ParsedWebPage readFromDisk(int workerId);
    int countFiles(int workerId);
}
