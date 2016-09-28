package com.jwm.searchservice;

import com.jwm.searchservice.document.RankedDocument;

import java.util.Set;

/**
 * Interface for the search engine webapp to use to interact
 *
 */
public interface SearchService
{
    Set<RankedDocument> getRankedDocumentsForQuery(String query);
}
