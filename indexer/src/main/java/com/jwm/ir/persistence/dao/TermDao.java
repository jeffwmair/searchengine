package com.jwm.ir.persistence.dao;

import com.jwm.ir.persistence.Term;

import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface TermDao {
    Term createOrIncrementTermFrequency(String term);
    List<Term> getDocumentTermsMatching(Set<String> strings);
}
