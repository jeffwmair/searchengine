package com.jwm.ir.search;

import com.jwm.searchservice.document.Document;
import com.jwm.searchservice.document.RankedDocument;

import java.util.*;

/**
 * Implementation (roughly) of FastCosineScore algorithm from Introduction to Information Retrieval, Chapter 7. (figure 7.1)
 */
public class FastCosineScoreCalculator {

	public Set<RankedDocument> scorePagesAgainstQuery(Map<Long, Document> documents,
													  Map<String, List<Document>> termPostings,
													  List<String> queryTerms,
													  int totalNumberOfDocuments) {

		if (totalNumberOfDocuments <= 0) throw new IllegalArgumentException("There must be at least 1 document indexed");
		if (queryTerms.size() == 0) throw new IllegalArgumentException("There must be at least 1 query term provided");

		Map<Long,Double> scores = new HashMap<>();

		for(String qt : queryTerms) {

			List<Document> termPostingsList = termPostings.get(qt);

			for(Document d : termPostingsList) {
				int docTermFrequency = d.getTermFrequency(qt);
				long documentId = d.getDocumentId();
				// note: can use an alternate weighting like idf, etc here instead.
				int weightedTermFrequency = docTermFrequency;
				initializeScoreIfNotThere(scores, documentId);
				double currentScore = scores.get(documentId);
				scores.put(documentId, currentScore + weightedTermFrequency);
			}
		}

		// normalize (?) the scores based on the document length
		for (Long documentId : scores.keySet()) {
			double normalizedScore = scores.get(documentId) / documents.get(documentId).getLength();
			scores.put(documentId, normalizedScore);
		}


		Set<RankedDocument> rankedDocuments = new TreeSet<>();
		for(Long documentId : scores.keySet()) {
			Document doc = documents.get(documentId);
			double score = scores.get(documentId);
			rankedDocuments.add(new RankedDocument(score, doc));
		}

		return rankedDocuments;
	}

	private void initializeScoreIfNotThere(Map<Long, Double> scores, Long documentId) {
		if (!scores.containsKey(documentId)) {
			scores.put(documentId, 0.0);
		}
	}

}
