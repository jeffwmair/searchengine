package jwm.ir.indexer;

/**
 * Created by Jeff on 2016-07-17.
 */
public class ParsedWebPageImpl implements ParsedWebPage {

    private final String url, content;
    public ParsedWebPageImpl(String url, String content) {

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
    public String getPageContent() {
        return content;
    }
}
