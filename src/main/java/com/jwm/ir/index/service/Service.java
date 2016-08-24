package com.jwm.ir.index.service;

import com.jwm.ir.entity.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public interface Service {

    enum FilterVerified { VerifiedOnly, UnverifiedOnly, Any }

    void addCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result);
    void addDocumentTerms(long pageId, Map<String, Integer> termFrequences);
    void addUrlForCrawling(String url, String parentUrl);
    void setUrlsAsVerified(List<String> verifiedUrls);
    void updatePageRanks(Map<Long, Double> pageRanks);
    void updateSummaries();
    boolean pageExists(String url);
    Page getPage(String url);
    List<Page> getPages(FilterVerified filterVerified);
    List<Page> getScoredPagesFromQuery(String query);
    List<String> getValidDomainExtensions();
    List<String> getUrlsToCrawl();

}
