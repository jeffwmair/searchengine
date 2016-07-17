package jwm.ir.indexer;

/**
 * Null-object pattern for ParsedWebPage interface
 * Created by Jeff on 2016-07-17.
 */
public class ParsedWebPageNoneImpl implements ParsedWebPage {
    @Override
    public String getUrl() {
        throw new RuntimeException("Cannot get a url from this type!");
    }

    @Override
    public String getPageContent() {
        throw new RuntimeException("Cannot get content from this type!");
    }
}
