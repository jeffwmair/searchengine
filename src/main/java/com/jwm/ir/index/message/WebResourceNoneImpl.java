package com.jwm.ir.index.message;

/**
 * Null-object pattern for ParsedWebPage interface
 * Created by Jeff on 2016-07-17.
 */
public class WebResourceNoneImpl implements WebResource {
    @Override
    public String getUrl() {
        throw new RuntimeException("Cannot get a url from this type!");
    }

    @Override
    public String getContent() {
        throw new RuntimeException("Cannot get content from this type!");
    }

}
