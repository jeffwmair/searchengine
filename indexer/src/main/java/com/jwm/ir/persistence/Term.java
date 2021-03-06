package com.jwm.ir.persistence;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "terms")
public class Term {

    @Id
    @GeneratedValue
    private long termId;

    @OneToMany
    @Column(name = "termId")
    public Collection<PageTerm> pageTerms;


    public Term() { }
    public Term(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    @Column(nullable = false, unique = true)
    private String term;

    @Column(nullable = false, name = "document_frequency")
    private int documentFrequency;

    public void incrementDocumentFrequency() {
        this.documentFrequency++;
    }
}
