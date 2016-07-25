package jwm.ir.domain;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "terms")
public class Term {

    @Id
    @GeneratedValue
    private long termId;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    public int getDocument_frequency() {
        return document_frequency;
    }

    public void setDocument_frequency(int document_frequency) {
        this.document_frequency = document_frequency;
    }

    @Column(nullable = false, unique = true)
    private String term;

    @Column(nullable = false)
    private int document_frequency;

}
