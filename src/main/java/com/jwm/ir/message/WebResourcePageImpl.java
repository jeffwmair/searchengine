package com.jwm.ir.message;

import com.jwm.ir.utils.AssertUtils;

/**
 * Created by Jeff on 2016-07-17.
 */
public class WebResourcePageImpl implements WebResource {

    private final String url, content;
    public WebResourcePageImpl(String url, String content) {

        AssertUtils.notEmpty(url, "Must provide url");
        AssertUtils.notNull(content, "Must provide content");
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
