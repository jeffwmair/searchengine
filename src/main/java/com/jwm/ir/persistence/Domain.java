package com.jwm.ir.persistence;

import com.jwm.ir.index.crawler.UrlUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name="domains")
public class Domain {

    private static final int CRAWLER_ID = 1;

    @Id
    @Column(name="domainId")
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String domain;

    @Column
    private int status;

    @Column
    private int crawlerId;

    @Column(name="total_crawls")
    private int totalCrawls;

    @Column
    private int locked;

    @Column(name="last_crawl")
    private Date lastCrawl;

    public Domain() { }
    public Domain(String domain) {
        this.domain = domain;
        this.crawlerId = CRAWLER_ID;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", status=" + status +
                ", crawlerId=" + crawlerId +
                ", totalCrawls=" + totalCrawls +
                ", locked=" + locked +
                ", lastCrawl=" + lastCrawl +
                '}';
    }

    /**
     * Increase total crawls by 1
     */
    public void incrementTotalCrawls() {
        this.totalCrawls++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Update the latest crawl time to now.
     */
    public void updateLastCrawl() {
        this.lastCrawl = new Date();
    }

    public void setStatus(int status) {
        this.status = status;
    }


    /**
     * Create a new instance from a full page url.
     * @param url
     * @return
     */
    public static Domain createFromUrl(String url) {
        return new Domain(UrlUtils.getDomainFromAbsoluteUrl(url));
    }

}
