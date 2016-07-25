package jwm.ir.domain;

/**
 * Created by Jeff on 2016-07-24.
 */
public class PageLink {
    private long id;
    private Page page;

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

    private Page destinationPage;
}
