package jwm.ir.utils;

/**
 * Created by Jeff on 2016-07-25.
 */
public interface Db {
    void addPerformanceStats(int verifications, int crawls, int indexes);
    void updateSummaries();
}
