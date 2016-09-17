package com.jwm.ir.webapp;

import com.jwm.searchservice.SearchService;
import com.jwm.searchservice.document.Document;
import com.jwm.searchservice.document.RankedDocument;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Jeff on 2016-09-17.
 */
public class SearchServiceStubImpl implements SearchService {
    @Override
    public Set<RankedDocument> getRankedDocumentsForQuery(String query) {
        Set<RankedDocument> docs = new TreeSet<RankedDocument>();
        for(int i = 0; i < 10; i++) {
            Document doc = new DocumentStubImpl("content "+i);
            docs.add(new RankedDocument(i/10, doc));
        }
        return docs;
    }
}
