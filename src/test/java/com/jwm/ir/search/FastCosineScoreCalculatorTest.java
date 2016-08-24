package com.jwm.ir.search;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-18
 */
public class FastCosineScoreCalculatorTest {

	private static final double ERROR_DELTA = 0.001;

	@Test
	public void zero_documents_indexed_should_throw_exception() {
		FastCosineScoreCalculator sut = new FastCosineScoreCalculator();
		try {
			sut.scorePagesAgainstQuery(null, null, null, 0);
			Assert.fail("should have thrown exception");
		}
		catch(IllegalArgumentException ex) { }
	}

	@Test
	public void basic_test() {
		FastCosineScoreCalculator sut = new FastCosineScoreCalculator();

		Document d1 = new DocumentImpl(1, "big blue dog");
		Document d2 = new DocumentImpl(2, "small red cat");
		Document d3 = new DocumentImpl(3, "big car");
		Map<Integer, Document> docsWithQueryTerm = new HashMap<>();
		docsWithQueryTerm.put(1, d1);
		// d2 doesn't have the query term
		docsWithQueryTerm.put(3, d3);

		Map<String, List<Document>> termPostings = new HashMap<>();
		termPostings.put("big", Arrays.asList(new Document[] { d1, d3 }));
		termPostings.put("blue", Arrays.asList(new Document[] { d1 }));
		termPostings.put("dog", Arrays.asList(new Document[] { d1 }));
		termPostings.put("small", Arrays.asList(new Document[] { d2 }));
		termPostings.put("red", Arrays.asList(new Document[] { d2 }));
		termPostings.put("cat", Arrays.asList(new Document[] { d2 }));
		termPostings.put("car", Arrays.asList(new Document[] { d3 }));

		List<String> queryTerms = Arrays.asList(new String[] {"big", "dog"});

		int totalDocumentsIndexed = 3;

		Set<RankedDocument> rankedDocuments = sut.scorePagesAgainstQuery(docsWithQueryTerm, termPostings, queryTerms, totalDocumentsIndexed);
		Iterator<RankedDocument> itr = rankedDocuments.iterator();
		Assert.assertEquals("first doc should have a score of 0.667", 0.667, itr.next().getRanking(), ERROR_DELTA);
		Assert.assertEquals("second doc should have a score of 0.5", 0.5, itr.next().getRanking(), ERROR_DELTA);

	}

}
