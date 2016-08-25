package com.jwm.ir.index.resource;

import org.springframework.util.Assert;

/**
 * Created by Jeff on 2016-07-17.
 */
public class WebResourcePageImpl implements WebResource {

    private final String url, content;
    public WebResourcePageImpl(String url, String content) {

        Assert.hasLength(url, "must provide url");
        Assert.notNull(content, "Must provide content");
        this.url = url;
        this.content = content;

    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getContent() {
        return content;
    }
}
