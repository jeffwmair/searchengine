package com.jwm.ir.index.crawler;

import com.jwm.ir.index.robotverifier.RobotDisallows;
import com.jwm.ir.index.robotverifier.RobotUserAgentCache;
import com.jwm.ir.utils.Clock;
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
        RobotDisallows disallows = new RobotDisallows(new ArrayList<String>());
        sut.add("google.com", disallows);

        RobotDisallows returned = sut.get("google.com");
        Assert.assertEquals(disallows, returned);

    }

    @Test
    public void verifyOldItemRemovedByCleanupAfter72HrsButNewerItemIsNotRemoved() {

        long seventyTwoHrsMilliseconds = 259200000;


        Clock clock = mock(Clock.class);
        // initial clock time
        RobotUserAgentCache sut = new RobotUserAgentCache(clock);

        RobotDisallows agent1 = new RobotDisallows(new ArrayList<String>());
        RobotDisallows agent2 = new RobotDisallows(new ArrayList<String>());

        // add the item
        when(clock.getTime()).thenReturn(0L);
        sut.add("google.com", agent1);
        when(clock.getTime()).thenReturn(seventyTwoHrsMilliseconds);
        sut.add("apple.com", agent2);

        // simulate 72 hrs passing
        when(clock.getTime()).thenReturn(seventyTwoHrsMilliseconds);
        sut.cleanup();

        RobotDisallows appleAgent = sut.get("apple.com");
        Assert.assertEquals(appleAgent, agent2);

    }
}
