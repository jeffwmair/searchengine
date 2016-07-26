package jwm.ir.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Jeff on 2016-07-23.
 */
@Entity
@Table(name="pages")
public class Page {

    @Id
    @Column(name="pageId")
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name="domainId")
    private Domain domain;

    @Column
    private String title;

    @Column
    private String description;

    @Column(nullable = false, unique = true)
    private String url;

    @Column
    private int verified;

    @Column(name = "fail_count")
    private int failCount;

    @Column
    private float pageRank;

    @Column(name="last_crawl")
    private Date lastCrawl;

    public Page(Domain domain, String pageUrl) {
        this.domain = domain;
        this.url = pageUrl;
    }

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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
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

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", domain=" + domain +
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

