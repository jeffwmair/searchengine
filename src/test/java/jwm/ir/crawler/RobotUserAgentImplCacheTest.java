package jwm.ir.crawler;

import jwm.ir.robotverifier.RobotUserAgent;
import jwm.ir.robotverifier.RobotUserAgentImpl;
import jwm.ir.robotverifier.RobotUserAgentCache;
import jwm.ir.robotverifier.RobotUserAgentNone;
import jwm.ir.utils.Clock;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotUserAgentImplCacheTest {

    @Test
    public void verifyItemInCacheAfterAdding() {

        Clock clock = mock(Clock.class);
        RobotUserAgentCache sut = new RobotUserAgentCache(clock);
        RobotUserAgentImpl agent = new RobotUserAgentImpl("agent", new ArrayList<String>());

        sut.add("google.com", agent);

        RobotUserAgent returnedAgent = sut.get("google.com");
        Assert.assertEquals(agent, returnedAgent);

    }

    @Test
    public void verifyOldItemRemovedByCleanupAfter72HrsButNewerItemIsNotRemoved() {

        long seventyTwoHrsMilliseconds = 259200000;


        Clock clock = mock(Clock.class);
        // initial clock time
        RobotUserAgentCache sut = new RobotUserAgentCache(clock);

        RobotUserAgent agent1 = new RobotUserAgentImpl("agent1", new ArrayList<String>());
        RobotUserAgent agent2 = new RobotUserAgentImpl("agent2", new ArrayList<String>());

        // add the item
        when(clock.getTime()).thenReturn(0L);
        sut.add("google.com", agent1);
        when(clock.getTime()).thenReturn(seventyTwoHrsMilliseconds);
        sut.add("apple.com", agent2);

        // simulate 72 hrs passing
        when(clock.getTime()).thenReturn(seventyTwoHrsMilliseconds);
        sut.cleanup();

        RobotUserAgent googleAgent = sut.get("google.com");
        RobotUserAgent appleAgent = sut.get("apple.com");
        Assert.assertTrue("Should be instance of RobotUserAgentNone", googleAgent instanceof RobotUserAgentNone);
        Assert.assertEquals(appleAgent, agent2);

    }
}
