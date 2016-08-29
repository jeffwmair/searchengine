package integration;

import com.jwm.ir.persistence.dao.*;
import com.jwm.ir.search.service.*;
import com.jwm.ir.search.*;
import org.hibernate.Session;

import org.junit.Test;

/**
 * Created by Jeff on 2016-08-25.
 */
public class SearchServiceImpl_do_query_basic extends DbTestBase {

    @Test
    public void do_query_test() {

		Session session = sessionFactory.openSession();
		TermDao termDao = new TermDaoImpl(session);
		PageDao pageDao = new PageDaoImpl(session);
		FastCosineScoreCalculator fastCosineCalculator = new FastCosineScoreCalculator();
		SearchServiceImpl sut = new SearchServiceImpl(fastCosineCalculator, termDao, pageDao);

		// need to create some document(s) to query

		session.close();
        
    }
}
