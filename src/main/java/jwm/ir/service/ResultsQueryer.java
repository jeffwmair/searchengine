package jwm.ir.service;

import jwm.ir.entity.Page;
import jwm.ir.entity.PageTerm;
import jwm.ir.entity.Term;
import jwm.ir.indexer.StemmerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeff on 2016-08-14.
 */
public class ResultsQueryer {

    private final List<Page> indexedPages;
    private final List<Term> terms;

    /**
     * New instance of ResultsQueryer with all the indexed pages to query.
     * @param indexedPages
     */
    public ResultsQueryer(List<Term> terms, List<Page> indexedPages) {
        this.indexedPages = indexedPages;
        this.terms = terms;
    }


    /**
     * Perform the query, return sorted list of matches with scores.
     * @param query
     * @return
     */
    public List<Page> queryPages(String query) {

        String query_lowercase = query.toLowerCase();
        Map<String, Integer> queryMap = QueryHelper.getMapOfQueryTerms(query_lowercase);

        List<Page> documents = new ArrayList<>();

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

            // add the page(s) associated with the matched term
            for(PageTerm pt : matchedTerm.pageTerms) {
                if (!documents.contains(pt.getPage())) {
                    documents.add(pt.getPage());
                }
            }


        }


        throw new RuntimeException("not implemented");
    }
}
