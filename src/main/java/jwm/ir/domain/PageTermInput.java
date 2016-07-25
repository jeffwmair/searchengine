package jwm.ir.domain;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "pageterm_input")
public class PageTermInput {

    public long getPageTermInputId() {
        return pageTermInputId;
    }

    public void setPageTermInputId(long pageTermInputId) {
        this.pageTermInputId = pageTermInputId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    @Id
    @GeneratedValue
    private long pageTermInputId;

    @ManyToOne
    @JoinColumn(name = "pageId")
    private Page page;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private int tf;

}
