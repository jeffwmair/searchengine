package com.jwm.ir.webapp;

import com.jwm.searchservice.document.Document;

/**
 * Created by Jeff on 2016-09-17.
 */
public class DocumentStubImpl implements Document {

    private final String content;
    public DocumentStubImpl(String content) {
        this.content = content;
    }

    @Override
    public long getDocumentId() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getTermFrequency(String term) {
        return 0;
    }
}
