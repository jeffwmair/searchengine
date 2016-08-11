package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.DaoFactory;
import jwm.ir.service.ServiceImpl;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-08.
 */
public class ServiceImpl_AddDocumentTerm_IntegrationTest {

    private ServiceImpl sut;
    private Db db;

    @Test
    public void add_document_Term_term_does_not_already_exist() {

        Domain domain = Domain.createFromUrl("google.com");
        Page page = Page.create(domain, "google.com/a");
        db.save(domain);
        db.save(page);
        long pageId = db.getPageIdFromUrl("google.com/a");
        Map<String, Integer> termFrequency = new HashMap<>();
        termFrequency.put("hello", 1);
        sut.addDocumentTerms(pageId, termFrequency);

        // get the term
        // get the page-term
    }

    @Before
    public void setup() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
        sut = new ServiceImpl(sessionFactory, new DaoFactory());
    }
}
