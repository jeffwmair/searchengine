package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-08.
 */
public class ServiceImpl_AddDocumentTerm_IntegrationTest extends DbTestBase {


    @Test
    public void add_document_Term_term_does_not_already_exist() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        save(domain);
        save(page);
        long pageId = fetchPageFromDb("google.com/a").getId();
        Map<String, Integer> termFrequency = new HashMap<>();
        termFrequency.put("hello", 1);
        ServiceImpl sut = new ServiceImpl(sessionFactory, new DaoFactory());
        sut.addDocumentTerms(pageId, termFrequency);

        // get the term
        // get the page-term
    }

}
