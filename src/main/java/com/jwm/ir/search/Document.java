package com.jwm.ir.search;

public interface Document {
	long getDocumentId();
	int getLength();
	int getTermFrequency(String term);
}
