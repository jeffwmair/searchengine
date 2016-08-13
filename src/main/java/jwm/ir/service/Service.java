package jwm.ir.service;

import jwm.ir.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public interface Service {
    void addUrlForCrawling(String url, String parentUrl);
    void addCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result);
    void addDocumentTerms(long pageId, Map<String, Integer> termFrequences);
    List<Page> getAllPages();
    boolean pageExists(String url);
    Page getPage(String url);
    List<String> getValidDomainExtensions();
    List<String> getUrlsToCrawl();
    void updatePageRanks(Map<Long, Double> pageRanks);
    List<String> getUnverifiedPageUrls();
    void setUrlsAsVerified(List<String> verifiedUrls);
}
