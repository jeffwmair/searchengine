package com.jwm.ir.search;

import java.util.HashMap;
import java.util.Map;

public class DocumentImpl implements Document {

	private final int id;
	private final int length;
	private final Map<String, Integer> termFrequency;
	public DocumentImpl(int id, String content) {
		this.id = id;
		String[] terms = content.split(" ");
		this.length = terms.length;

		this.termFrequency = new HashMap<>();
		for(String term : terms) {
			if (!termFrequency.containsKey(term)) {
				termFrequency.put(term, 0);
			}

			termFrequency.put(term, termFrequency.get(term) + 1);
		}
	}

	public int getDocumentId() {
		return id;
	}

	public int getLength() {
		return length;
	}

	public int getTermFrequency(String term) {
		if (!termFrequency.containsKey(term)) {
			return 0;
		}

		return termFrequency.get(term);
	}
}
