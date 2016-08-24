package com.jwm.ir.entity.dao;

import com.jwm.ir.entity.Page;

import java.util.Map;

/**
 * Created by Jeff on 2016-08-01.
 */
public interface PageDao {
    boolean pageExists(String url);
    Page getPage(String url);
    Page getPage(long pageId);
    Page create(String url, DomainDao domainDao);
    void setPageCrawlResult(String url, String pageTitle, String pageDesc, Page.CrawlResult result);
    void updatePageRanks(Map<Long,Double> pageRanks);
}
