package jwm.ir.entity;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */

@Entity
@Table(name="pagelinks")
public class PageLink {

    public static PageLink create(Page parent, Page page) {
        PageLink pl = new PageLink();
        pl.setPage(parent);
        pl.setDestinationPage(page);
        return pl;
    }

    @Id
    @Column
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "pageId")
    private Page page;

    @ManyToOne
    @JoinColumn(name = "destPageId")
    private Page destinationPage;

    @Override
    public String toString() {
        return "PageLink{" +
                "id=" + id +
                ", page=" + page +
                ", destinationPage=" + destinationPage +
                '}';
    }

    public Page getDestinationPage() {
        return destinationPage;
    }

    public void setDestinationPage(Page destinationPage) {
        this.destinationPage = destinationPage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public long getSourcePageId() {
        return getPage().getId();
    }

    public long getDestinationPageId() {
        return getDestinationPage().getId();
    }
}
