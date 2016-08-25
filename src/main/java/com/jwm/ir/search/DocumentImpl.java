package com.jwm.ir.search;

import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.PageTerm;

import java.util.HashMap;
import java.util.Map;

public class DocumentImpl implements Document {

	private final long id;
	private final int length;
	private final Map<String, Integer> termFrequency;

	/**
	 * Create a new Document from Page (entity) object
	 * @param page
     */
	public DocumentImpl(Page page) {
		this.id = page.getId();
		this.termFrequency = new HashMap<>();
		int localLength = 0;
		for(PageTerm pageTerm : page.getPageTerms()) {
			localLength += pageTerm.getTermFrequency();
			this.termFrequency.put(pageTerm.getTerm().getTerm(), pageTerm.getTermFrequency());
		}
		this.length = localLength;
	}

	/**
	 * Create a new Document from String content
	 * @param id
	 * @param content
     */
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

	public long getDocumentId() {
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
