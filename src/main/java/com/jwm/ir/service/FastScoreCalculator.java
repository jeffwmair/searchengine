package com.jwm.ir.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastScoreCalculator {

	public Map<Integer,Double> scorePagesAgainstQuery(Map<Integer, Document> documents, 
			Map<String, List<Document>> termPostings,
			List<String> queryTerms, 
			int totalNumberOfDocuments, 
			Map<String, Integer> documentFrequencies) {

		if (totalNumberOfDocuments <= 0) throw new IllegalArgumentException("There must be at least 1 document indexed");
		if (queryTerms.size() == 0) throw new IllegalArgumentException("There must be at least 1 query term provided");

		Map<Integer,Double> scores = new HashMap<>();

		for(String qt : queryTerms) {

			double weightedQueryTerm = calculateIdf(totalNumberOfDocuments, documentFrequencies.get(qt));
			List<Document> termPostingsList = termPostings.get(qt);

			for(Document d : termPostingsList) {
				int docTermFrequency = d.getTermFrequency(qt);
				int documentId = d.getDocumentId();
				// note: can use an alternate weighting like idf, etc here instead.
				int weightedTermFrequency = docTermFrequency;
				initializeScoreIfNotThere(scores, documentId);
				double currentScore = scores.get(documentId);
				scores.put(documentId, currentScore + weightedTermFrequency);
			}
		}

		// normalize (?) the scores based on the document length
		for (Integer documentId : scores.keySet()) {
			double normalizedScore = scores.get(documentId) / documents.get(documentId).getLength();
			scores.put(documentId, normalizedScore);
		}


		return scores;
	}

	private void initializeScoreIfNotThere(Map<Integer, Double> scores, Integer documentId) {
		if (!scores.containsKey(documentId)) {
			scores.put(documentId, 0.0);
		}
	}

	/**
	 * Calculate IDF - inverse document frequency
	 */
	private double calculateIdf(int totalNumberOfDocuments, int termFrequency) {
		return Math.log10(totalNumberOfDocuments / termFrequency);
	}

}
