package jwm.ir.domain;

/**
 * Created by Jeff on 2016-07-23.
 */
public class Page {
    private long id;
    private long domainId;
    private String title, description;

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

    @Override
    public String toString() {
        return "id:'"+id+"', domainId:'"+domainId+"', title:'"+title+"', desc:'"+description+"'";
    }
}
