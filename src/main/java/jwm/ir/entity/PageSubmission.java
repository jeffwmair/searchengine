package jwm.ir.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "pagesubmissions")
public class PageSubmission {

    @Id
    @Column
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "pageId")
    private Page page;

    @Column
    private Date submitDate;

    @Override
    public String toString() {
        return "PageSubmission{" +
                "id=" + id +
                ", page=" + page +
                ", submitDate=" + submitDate +
                ", Ip='" + Ip + '\'' +
                '}';
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

    @Column
    private String Ip;
}
