package jwm.ir.utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public interface Db {
    void addPerformanceStats(int verifications, int crawls, int indexes);
    void updateSummaries();
    void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults);
    List<String> getUnverifiedPagesForVerification();
}
