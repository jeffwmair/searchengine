package jwm.ir.domain.persistence;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface PageTermDao {
    void create(long pageId, String termValue, int termFrequency);
}
