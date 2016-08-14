package jwm.ir.entity.dao;

import jwm.ir.entity.Term;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface TermDao {
    Term createOrIncrementTermFrequency(String term);
}
