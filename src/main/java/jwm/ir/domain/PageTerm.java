package jwm.ir.domain;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "pageterms")
public class PageTerm {

    @Id
    @GeneratedValue
    private long postingTermId;

    @ManyToOne
    @JoinColumn(name = "pageId")
    private Page page;

    @ManyToOne
    @JoinColumn(name = "termId")
    private Term term;

    @Column(nullable = false)
    private int term_frequency;

    public long getPostingTermId() {
        return postingTermId;
    }

    public void setPostingTermId(long postingTermId) {
        this.postingTermId = postingTermId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getTerm_frequency() {
        return term_frequency;
    }

    public void setTerm_frequency(int term_frequency) {
        this.term_frequency = term_frequency;
    }
}
