package com.jwm.searchservice.document;

public interface Document {
	long getDocumentId();
	int getLength();
	int getTermFrequency(String term);
}
