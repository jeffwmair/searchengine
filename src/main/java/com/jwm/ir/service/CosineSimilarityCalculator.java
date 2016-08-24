package com.jwm.ir.service;

import java.util.List;

public class CosineSimilarityCalculator {

	public static double calculateSimilarity(List<Integer> a, List<Integer> b) {

		if (a.size() != b.size()) {
			throw new IllegalArgumentException("cannot calculate the similarity between vectors with different sizes.  Given sizes were:"+a.size()+" and "+b.size());
		}

		if (a.size() == 0 || b.size() == 0) {
			throw new IllegalArgumentException("cannot calculate the similarity between vectors of size 0");
		}

		double numerator = 0.0;
		double denomQry = 0.0;
		double denomDoc = 0.0;
		
		double vector_length = a.size();
			
		for (int i = 0; i < vector_length; i++) {
			numerator += (a.get(i) * b.get(i));
			denomDoc += Math.pow(b.get(i), 2);
			denomQry += Math.pow(a.get(i), 2);
		}
		
		double denom = Math.sqrt(denomDoc * denomQry);
		double similarityScore = numerator / denom;

		return similarityScore;

	}
}
