package com.jwm.ir.search;

import com.jwm.ir.search.Document;
import com.jwm.ir.search.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;

public class DocumentImplTest {

	@Test
	public void has_correct_length_test() {
		Document sut = new DocumentImpl(1, "one two three");
		Assert.assertEquals("should be 3 items", 3, sut.getLength());
	}

	@Test
	public void has_term_frequency_for_existing_terms() {
		Document sut = new DocumentImpl(1, "one two two three three three");
		Assert.assertEquals(1, sut.getTermFrequency("one"));
		Assert.assertEquals(2, sut.getTermFrequency("two"));
		Assert.assertEquals(3, sut.getTermFrequency("three"));
	}

	@Test
	public void has_zero_term_frequency_for_non_existing_terms() {
		Document sut = new DocumentImpl(1, "one two two three three three");
		Assert.assertEquals(0, sut.getTermFrequency("foobar"));
	}

}
