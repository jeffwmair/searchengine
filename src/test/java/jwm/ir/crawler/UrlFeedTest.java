package jwm.ir.crawler;

import jwm.ir.utils.Db;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeedTest {

    @Test
    public void test() {

        BlockingQueue<String> output = new LinkedBlockingQueue<>();
        Db db = mock(Db.class);
        List<String> urls = new ArrayList<>();
        urls.add("url1");
        urls.add("url2");
        when(db.popUrls()).thenReturn(urls);
        UrlFeed sut = new UrlFeed(db, output);

        sut.process();
        Assert.assertTrue("url1".equals(output.remove()));
        Assert.assertTrue("url2".equals(output.remove()));
        try {
            output.remove();
            Assert.fail("should throw a NPE; only 2 items in the queue");
        }
        catch (NoSuchElementException ex) {}

    }

}
