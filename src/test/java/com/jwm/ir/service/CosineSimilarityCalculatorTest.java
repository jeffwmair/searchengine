package com.jwm.ir.service;

import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-18
 */
public class CosineSimilarityCalculatorTest {

	private static final double ERROR_DELTA = 0.001;

	@Test
	public void different_size_vectors_should_throw_exception() {
		try {
			List<Integer> a = new ArrayList<>();
			List<Integer> b = new ArrayList<>();
			a.add(1);
			a.add(2);
			b.add(1);
			CosineSimilarityCalculator.calculateSimilarity(a, b);
			Assert.fail("should have thrown exception");
		}
		catch(IllegalArgumentException ex) { }
	}

	@Test
	public void size_zero_vectors_should_throw_exception() {
		try {
			List<Integer> a = new ArrayList<>();
			List<Integer> b = new ArrayList<>();
			CosineSimilarityCalculator.calculateSimilarity(a, b);
			Assert.fail("should have thrown exception");
		}
		catch(IllegalArgumentException ex) { }

	}

	@Test 
	public void simple_1_1_vectors_should_produce_1() {
		List<Integer> a = new ArrayList<>();
		List<Integer> b = new ArrayList<>();
		a.add(1);
		b.add(1);
		double score = CosineSimilarityCalculator.calculateSimilarity(a, b);
		Assert.assertEquals("score should be 1 but was"+score, 1.0, score, ERROR_DELTA);
	}

	@Test
	public void arbitrary_vector_scoring() {

		/**
		 * From here:
		 * http://stackoverflow.com/questions/1746501/can-someone-give-an-example-of-cosine-similarity-in-a-very-simple-graphical-wa
		 * a: [2, 1, 0, 2, 0, 1, 1, 1]
		 *
		 * b: [2, 1, 1, 1, 1, 0, 1, 1]
		 * The cosine of the angle between them is about 0.822.
		 */

		List<Integer> a = Arrays.asList(2, 1, 0, 2, 0, 1, 1, 1);
		List<Integer> b = Arrays.asList(2, 1, 1, 1, 1, 0, 1, 1);
		double score = CosineSimilarityCalculator.calculateSimilarity(a, b);
		Assert.assertEquals("score should be 0.822 but was"+score, 0.822, score, ERROR_DELTA);

	}


}
