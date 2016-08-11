package jwm.ir.utils;

import jwm.ir.domain.Page;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public interface Db {
    void save(Object entity);
    Page getPage(String url);
    void addPerformanceStats(int verifications, int crawls, int indexes);
    void updateSummaries();
    void addDocumentTerms(String json, long pageId);
    List<String> getPageLinks(List<String> pageIds);
    String[] getPageIdsGreaterThanPageId(String lagePageReceived, int limit);
    void updatePageRanks(HashMap<Integer,Double> pageRanks);
    long getPageIdFromUrl(String url);
    void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults);
    List<String> getUnverifiedPagesForVerification();
}
