package integration;

import com.jwm.ir.persistence.dao.*;
import com.jwm.ir.search.FastCosineScoreCalculator;
import com.jwm.ir.search.service.SearchServiceImpl;
import org.hibernate.Session;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-25.
 */
public class SearchServiceImpl_do_query_basic extends DbTestBase {

    @Test
    public void do_query_test() {

		FastCosineScoreCalculator fastCosineCalculator = new FastCosineScoreCalculator();
		SearchServiceImpl sut = new SearchServiceImpl(fastCosineCalculator, sessionFactoryProvider, new DaoFactory());

		// need to create some document(s) to query


    }
}
