package jwm.ir.entity.dao;

import jwm.ir.entity.Term;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Created by Jeff on 2016-08-05.
 */
public class TermDaoImpl implements TermDao {

    private final Session session;

    public TermDaoImpl(Session session) {
        this.session = session;
    }

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
