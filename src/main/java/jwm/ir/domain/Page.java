package jwm.ir.domain;

import java.util.Date;

/**
 * Created by Jeff on 2016-07-23.
 */
public class Page {
    private long id, domainId;
    private String title, description, url;
    private int verified, failCount;
    private float pageRank;
    private Date lastCrawl;

    public Date getLastCrawl() {
        return lastCrawl;
    }

    public void setLastCrawl(Date lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    public Page() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public float getPageRank() {
        return pageRank;
    }

    public void setPageRank(float pageRank) {
        this.pageRank = pageRank;
    }

    public Page(int domainId, String url, float page_rank) {
        this.domainId = domainId;
        this.url = url;
        this.pageRank = page_rank;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", domainId=" + domainId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", verified=" + verified +
                ", failCount=" + failCount +
                ", pageRank=" + pageRank +
                ", lastCrawl=" + lastCrawl +
                '}';
    }
}

