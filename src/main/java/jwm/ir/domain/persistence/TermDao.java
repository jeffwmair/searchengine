package jwm.ir.domain.persistence;

import jwm.ir.domain.Term;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface TermDao {
    Term createOrIncrementTermFrequency(String term);
}