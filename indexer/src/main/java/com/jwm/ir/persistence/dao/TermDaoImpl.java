package com.jwm.ir.persistence.dao;

import com.jwm.ir.persistence.Term;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 2016-08-05.
 */
public class TermDaoImpl implements TermDao {

    private final Session session;

    public TermDaoImpl(Session session) {
        this.session = session;
    }

    /**
     * Add the document Term to the database if not exists, otherwise just increment its Document Frequency
     * @param termValue
     * @return
     */
    @Override
    public Term createOrIncrementTermFrequency(String termValue) {
        Term term;
        if (exists(termValue)) {
            term = getTerm(termValue);
        }
        else {
            term = new Term(termValue);
        }

        term.incrementDocumentFrequency();
        session.saveOrUpdate(term);
        return term;
    }

    @Override
    public List<Term> getDocumentTermsMatching(Set<String> terms) {
        return session.createCriteria(Term.class).add(Restrictions.in("term", terms)).list();
    }

    private Term getTerm(String term) {
        return (Term)getTermObject(term);
    }

    private boolean exists(String term) {
        return getTermObject(term) != null;
    }

    private Object getTermObject(String term) {
        return session.createCriteria(Term.class).add(Restrictions.eq("term", term)).uniqueResult();
    }
}
