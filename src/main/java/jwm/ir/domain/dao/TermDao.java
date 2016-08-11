package jwm.ir.domain.dao;

import jwm.ir.domain.Term;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface TermDao {
    Term createOrIncrementTermFrequency(String term);
}
