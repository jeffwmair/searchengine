package com.jwm.ir.search.service;

import com.jwm.ir.search.document.RankedDocument;

import java.util.Set;

/**
 * Created by Jeff on 2016-08-25.
 */
public interface SearchService {
    Set<RankedDocument> getRankedDocumentsForQuery(String query);
}
