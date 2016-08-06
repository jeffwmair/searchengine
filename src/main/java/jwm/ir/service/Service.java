package jwm.ir.service;

import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public interface Service {
    void addUrlForCrawling(String url, String parentUrl);
    void addDocumentTerms(long pageId, Map<String, Integer> termFrequences);
}
