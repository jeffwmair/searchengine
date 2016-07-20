package jwm.ir.robotverifier;


import jwm.ir.utils.StringBuilderWithNewline;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParserTest {

    @Test
    public void getDisallowValueTest() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /abc");

        // act
        RobotTxtParser parser = new RobotTxtParser(sb.toString());
        List<RobotUserAgent> agents = parser.parseAgents();

        // assert
        RobotUserAgent agent = agents.get(0);
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
        RobotTxtParser parser = new RobotTxtParser(sb.toString());
        List<RobotUserAgent> agents = parser.parseAgents();

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
        RobotTxtParser parser = new RobotTxtParser(sb.toString());
        List<RobotUserAgent> agents = parser.parseAgents();

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
        RobotTxtParser parser = new RobotTxtParser(sb.toString());
        List<RobotUserAgent> agents = parser.parseAgents();

        // assert
        Assert.assertEquals(2, agents.size());

    }

    @Test
    public void getZeroUserAgentsTest() {

         // arrange
        String content = "";

        // act
        RobotTxtParser parser = new RobotTxtParser(content);
        List<RobotUserAgent> agents = parser.parseAgents();

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
        RobotTxtParser parser = new RobotTxtParser(sb.toString());
        List<RobotUserAgent> agents = parser.parseAgents();

        // assert
        Assert.assertEquals(1, agents.size());

    }


}
