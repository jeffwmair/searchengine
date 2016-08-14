package jwm.ir.service;

import jwm.ir.entity.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public interface Service {

    public enum FilterVerified { VerifiedOnly, UnverifiedOnly, Any }

    void addCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result);
    void addDocumentTerms(long pageId, Map<String, Integer> termFrequences);
    void addUrlForCrawling(String url, String parentUrl);
    void setUrlsAsVerified(List<String> verifiedUrls);
    void updatePageRanks(Map<Long, Double> pageRanks);
    void updateSummaries();
    boolean pageExists(String url);
    Page getPage(String url);
    List<Page> getAllPages(FilterVerified filterVerified);
    List<String> getValidDomainExtensions();
    List<String> getUrlsToCrawl();
    List<String> getUnverifiedPageUrls();

}
