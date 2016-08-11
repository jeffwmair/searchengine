package jwm.ir.domain;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.persistence.DomainRepository;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 2016-07-23.
 */
@Entity
@Table(name="pages")
public class Page {

    /**
     * Create a new page object from a given url
     * @param url
     * @return
     */
    public static Page create(String url, DomainRepository domainRepository) {
        Page p = new Page();
        p.setUrl(url);
        String domainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        Domain d;
        if (domainRepository.domainExists(domainName)) {
            d = domainRepository.getDomain(domainName);
        }
        else {
            d = Domain.createFromUrl(url);
        }
        p.setDomain(d);
        return p;
    }

    public static Page create(Domain domain, String url) {
        Page p = new Page();
        p.setUrl(url);
        p.setDomain(domain);
        return p;
    }

    @Id
    @Column(name="pageId")
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name="domainId")
    private Domain domain;

    @OneToMany
    @JoinColumn(name = "pageId")
    private Set<PageTerm> postingTermId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "pageId")
    private Set<PageLink> pageLinks;

    public Set<PageLink> getPageLinks() {
        return pageLinks;
    }


    public Set<PageTerm> getPageTerms() {
        return postingTermId;
    }

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

    public Date getLastCrawl() {
        return lastCrawl;
    }

    public void setLastCrawl(Date lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    public void updateFromCrawl(String title, String description, CrawlResult result) {
        this.lastCrawl = new Date();
        this.title = title;
        this.description = description;
        updateFailCount(result);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return url.equals(page.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
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

    public enum CrawlResult { Success, Fail }

    /**
     * Update the fail count by -1 or 1 if successful or unsuccessful.
     * @param result
     */
    public void updateFailCount(CrawlResult result) {
        failCount += Math.max(0, result == CrawlResult.Fail ? 1 : -1);
    }
}

