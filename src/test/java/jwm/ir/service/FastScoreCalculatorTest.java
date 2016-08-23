package jwm.ir.service;

import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-18
 */
public class FastScoreCalculatorTest {

	private static final double ERROR_DELTA = 0.001;

	@Test
	public void zero_documents_indexed_should_throw_exception() {
		FastScoreCalculator sut = new FastScoreCalculator();
		try {
			sut.scorePagesAgainstQuery(null, null, null, 0, null);
			Assert.fail("should have thrown exception");
		}
		catch(IllegalArgumentException ex) { }
	}

	@Test
	public void basic_test() {
		FastScoreCalculator sut = new FastScoreCalculator();
		int N = 3;

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

		Map<String, Integer> documentFrequencies = new HashMap<>();
		documentFrequencies.put("big", 2);
		documentFrequencies.put("blue", 1);
		documentFrequencies.put("dog", 1);
		documentFrequencies.put("small", 1);
		documentFrequencies.put("red", 1);
		documentFrequencies.put("cat", 1);
		documentFrequencies.put("car", 1);

		Map<Integer,Double> scores = sut.scorePagesAgainstQuery(docsWithQueryTerm, termPostings, queryTerms, totalDocumentsIndexed, documentFrequencies);
		for(Integer docId : scores.keySet()) {
			System.out.println("doc:'"+docId+"', score:'"+scores.get(docId)+"'");
		}


	}

}
