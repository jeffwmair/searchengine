package com.jwm.ir.search;

public interface Document {
	int getDocumentId();
	int getLength();
	int getTermFrequency(String term);
}
