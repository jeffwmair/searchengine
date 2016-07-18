package jwm.ir.indexer.queue;

import jwm.ir.indexer.ParsedWebPage;
import jwm.ir.indexer.ParsedWebPageImpl;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-17.
 */
public class IndexQueueFileSysImplTest {

    @Test
    public void putAndCountTest() {

        // arrange
        int workerId = 1;
        IndexFileSys fileSys = mock(IndexFileSys.class);
        IndexQueueFileSysImpl sut = new IndexQueueFileSysImpl(fileSys);
        ParsedWebPage parsedWebPage = new ParsedWebPageImpl("url", "content");

        // act
//        sut.put(parsedWebPage);
//
//        // assert
//        Assert.assertEquals(1, sut.getSize());

    }
}
