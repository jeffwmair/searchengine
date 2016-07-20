package jwm.ir.robotverifier;


import jwm.ir.utils.Clock;
import jwm.ir.utils.StringBuilderWithNewline;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParserTest {

    private RobotTxtParser sut;
    private Clock clock;

    @Before
    public void setup() {
        clock = mock(Clock.class);
        sut = new RobotTxtParser(clock);
    }

    @Test
    public void getDisallowValueTest() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /abc");

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(sb.toString());

        // assert
        RobotUserAgentImpl agent = agents.get(0);
        List<String> disallows = agent.getDisallows();

        Assert.assertEquals(1, disallows.size());
        Assert.assertEquals("/abc", disallows.get(0));

    }

    @Test
    public void getSingleUserAgentTest() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /");

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(sb.toString());

        // assert
        Assert.assertEquals(1, agents.size());

    }

    @Test
    public void get2AgentsWithNoLineSeparator() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: fooo");
        sb.appendLine("Disallow: /");
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /");

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(sb.toString());

        // assert
        Assert.assertEquals(2, agents.size());

    }

    @Test
    public void get2AgentsSeparatedByNewline() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: fooo");
        sb.appendLine("Disallow: /");
        sb.appendLine("");
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /");

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(sb.toString());

        // assert
        Assert.assertEquals(2, agents.size());

    }

    @Test
    public void getZeroUserAgentsTest() {

         // arrange
        String content = "";

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(content);

        // assert
        Assert.assertEquals(0, agents.size());
    }

    @Test
    public void getAgentsFromUnusualCasing() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("USER-AGENT: fooo");
        sb.appendLine("DISALLOW: /");

        // act
        List<RobotUserAgentImpl> agents = sut.parseAgents(sb.toString());

        // assert
        Assert.assertEquals(1, agents.size());

    }


}
