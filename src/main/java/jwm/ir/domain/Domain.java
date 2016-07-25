package jwm.ir.domain;

import java.util.Date;

/**
 * Created by Jeff on 2016-07-24.
 */
public class Domain {
    private long id;
    private String domain;
    private int status, crawlerId, totalCrawls, locked;
    private Date lastCrawl;

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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(int crawlerId) {
        this.crawlerId = crawlerId;
    }

    public int getTotalCrawls() {
        return totalCrawls;
    }

    public void setTotalCrawls(int totalCrawls) {
        this.totalCrawls = totalCrawls;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public Date getLastCrawl() {
        return lastCrawl;
    }

    public void setLastCrawl(Date lastCrawl) {
        this.lastCrawl = lastCrawl;
    }
}
