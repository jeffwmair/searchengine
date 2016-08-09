package jwm.ir.domain;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "pageterms", uniqueConstraints = @UniqueConstraint(columnNames = {"pageId", "termId"}))
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

    @Column(nullable = false, name = "term_frequency")
    private int termFrequency;

    public PageTerm() {}
    public PageTerm(Page page, Term term, int termFrequency) {
        this.page = page;
        this.term = term;
        this.termFrequency = termFrequency;

    }

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

    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }
}
