package integration;

import com.jwm.ir.persistence.Term;
import com.jwm.ir.persistence.dao.TermDaoImpl;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Jeff on 2016-08-25.
 */
public class TermDao_Fetch_Terms_IntegrationTest extends DbTestBase {

    @Test
    public void fetch_terms_test() {

        Session session = sessionFactory.openSession();
        TermDaoImpl sut = new TermDaoImpl(session);
        // create a term to be fetched
        sut.createOrIncrementTermFrequency("dog");

        // fetch any of these terms that exist
        Set<String> termsToFind = new TreeSet<>();
        termsToFind.add("dog");
        termsToFind.add("cat");
        termsToFind.add("rabbit");
        List<Term> terms = sut.getDocumentTermsMatching(termsToFind);
        session.close();

        Assert.assertEquals("should be 1 term found", 1, terms.size());
        Assert.assertEquals("should be term 'dog'", "dog", terms.get(0).getTerm());
    }
}
