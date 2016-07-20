package jwm.ir.message;

/**
 * Created by Jeff on 2016-07-17.
 */
public class WebResourcePageImpl implements WebResource {

    private final String url, content;
    public WebResourcePageImpl(String url, String content) {

        if (url == null || url.isEmpty()) throw new IllegalArgumentException("Must provide url");
        if (content == null || content.isEmpty()) throw new IllegalArgumentException("Must provide content");
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
