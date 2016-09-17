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
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;

/**
 * Created by Jeff on 2016-08-14.
 */
public class SearchServiceImpl implements SearchService {

    private final FastCosineScoreCalculator scoreCalculator;
    private final DaoFactory daoFactory;
    private final SessionFactory sessionFactory;

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

        String query_lowercase = query.toLowerCase();
        Map<String, Integer> queryMap = StemmerWrapper.convertToStemmed(QueryHelper.getMapOfQueryTerms(query_lowercase));

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

        session.close();
        return scoreCalculator.scorePagesAgainstQuery(documents, termPostings, queryTerms, totalIndexedPages);

    }
}
