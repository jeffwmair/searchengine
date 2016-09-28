package com.jwm.ir.search.service;

import com.jwm.ir.index.StemmerWrapper;
import com.jwm.ir.persistence.PageTerm;
import com.jwm.ir.persistence.SessionFactoryProvider;
import com.jwm.ir.persistence.Term;
import com.jwm.ir.persistence.dao.DaoFactory;
import com.jwm.ir.persistence.dao.PageDao;
import com.jwm.ir.persistence.dao.TermDao;
import com.jwm.ir.search.FastCosineScoreCalculator;
import com.jwm.ir.search.QueryHelper;
import com.jwm.ir.search.document.DocumentImpl;
import com.jwm.searchservice.SearchService;
import com.jwm.searchservice.document.Document;
import com.jwm.searchservice.document.RankedDocument;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by Jeff on 2016-08-14.
 */
public class SearchServiceImpl implements SearchService {

    private final FastCosineScoreCalculator scoreCalculator;
    private final DaoFactory daoFactory;
    private final SessionFactory sessionFactory;
    private static final Logger log = LogManager.getLogger(SearchServiceImpl.class);

    /**
     * New instance of SearchServiceImpl with all the indexed pages to query.
     */
    public SearchServiceImpl(FastCosineScoreCalculator scoreCalculator,
                             SessionFactoryProvider sessionFactoryProvider,
                             DaoFactory daoFactory) {
        this.scoreCalculator = scoreCalculator;
        this.daoFactory = daoFactory;
        this.sessionFactory = sessionFactoryProvider.getSessionFactory();
    }


    /**
     * Perform the query, return sorted list of matches with scores.
     * @param query
     * @return
     */
    @Override
    public Set<RankedDocument> getRankedDocumentsForQuery(String query) {


        if (log.isDebugEnabled()) {
            log.debug("getRankedDocumentsForQuery:'"+query+"'");
        }
        String query_lowercase = query.toLowerCase();
        Assert.hasLength(query_lowercase, "non-empty query must be provided!");
        Map<String, Integer> queryMap = StemmerWrapper.convertToStemmed(QueryHelper.getMapOfQueryTerms(query_lowercase));
        Assert.notEmpty(queryMap.keySet(), "stemmed queryMap has an empty keyset!  This was not expected");

        List<String> queryTerms = new ArrayList<>();
        Session session = sessionFactory.openSession();
        TermDao termDao = daoFactory.createTermDao(session);
        PageDao pageDao = daoFactory.createPageRepository(session);
        List<Term> terms = termDao.getDocumentTermsMatching(queryMap.keySet());
        int totalIndexedPages = pageDao.getIndexedPageCount();

        Map<Long, Document> documents = new HashMap<>();
        Map<String, List<Document>> termPostings = new HashMap<>();
        for (String term_raw : queryMap.keySet()) {

            String term_stemmed = StemmerWrapper.stem(term_raw);
            Term matchedTerm = null;

            // try to find a matching term in the database
            for(Term term : terms) {
                if (term.getTerm().equals(term_stemmed)) {
                    matchedTerm = term;
                    break;
                }
            }

            // no match, so check the next query term
            if (matchedTerm == null) {
                continue;
            }

            List<Document> postingsList = new ArrayList<>();
            termPostings.put(term_stemmed, postingsList);

            // add the page(s) associated with the matched term
            for(PageTerm pt : matchedTerm.pageTerms) {
                Document document = new DocumentImpl(pt.getPage());
                if (!documents.containsKey(document.getDocumentId())) {
                    documents.put(document.getDocumentId(), new DocumentImpl(pt.getPage()));
                }

                if (!postingsList.contains(document)) {
                    postingsList.add(document);
                }
            }

            queryTerms.add(term_stemmed);
        }

        Assert.notEmpty(queryTerms, "None of the given query terms had any matches in any pages.  Need to improve indexing!");

        session.close();
        return scoreCalculator.scorePagesAgainstQuery(documents, termPostings, queryTerms, totalIndexedPages);

    }
}
