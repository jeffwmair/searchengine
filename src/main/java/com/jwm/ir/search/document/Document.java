package com.jwm.ir.search.document;

public interface Document {
	long getDocumentId();
	int getLength();
	int getTermFrequency(String term);
}
