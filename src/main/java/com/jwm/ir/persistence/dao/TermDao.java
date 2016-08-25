package com.jwm.ir.persistence.dao;

import com.jwm.ir.persistence.Term;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface TermDao {
    Term createOrIncrementTermFrequency(String term);
}
