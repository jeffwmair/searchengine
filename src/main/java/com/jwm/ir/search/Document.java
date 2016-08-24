package com.jwm.ir.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Document {
	int getDocumentId();
	int getLength();
	int getTermFrequency(String term);
}
