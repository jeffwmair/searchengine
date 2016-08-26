package com.jwm.ir.search.service;

import com.jwm.ir.persistence.PageTerm;
import com.jwm.ir.persistence.Term;
import com.jwm.ir.index.StemmerWrapper;
import com.jwm.ir.search.*;
import com.jwm.ir.search.document.Document;
import com.jwm.ir.search.document.DocumentImpl;
import com.jwm.ir.search.document.RankedDocument;

import java.util.*;

/**
 * Created by Jeff on 2016-08-14.
 */
public class SearchServiceImpl implements SearchService {

    private final int totalIndexedPages;
    private final List<Term> terms;
    private final FastCosineScoreCalculator scoreCalculator;

    /**
     * New instance of SearchServiceImpl with all the indexed pages to query.
     */
    public SearchServiceImpl(List<Term> terms,
                             int totalIndexedPages,
                             FastCosineScoreCalculator scoreCalculator) {
        this.totalIndexedPages = totalIndexedPages;
        this.terms = terms;
        this.scoreCalculator = scoreCalculator;
    }


    /**
     * Perform the query, return sorted list of matches with scores.
     * @param query
     * @return
     */
    @Override
    public Set<RankedDocument> getRankedDocumentsForQuery(String query) {

        String query_lowercase = query.toLowerCase();
        Map<String, Integer> queryMap = QueryHelper.getMapOfQueryTerms(query_lowercase);

        Map<Long, Document> documents = new HashMap<>();
        Map<String, List<Document>> termPostings = new HashMap<>();
        List<String> queryTerms = new ArrayList<>();

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

        return scoreCalculator.scorePagesAgainstQuery(documents, termPostings, queryTerms, totalIndexedPages);

    }
}
