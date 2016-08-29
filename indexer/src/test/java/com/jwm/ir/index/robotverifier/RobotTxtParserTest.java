package com.jwm.ir.index.robotverifier;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParserTest {

    private RobotTxtParser sut;

    @Before
    public void setup() {
        sut = new RobotTxtParser();
    }

    @Test
    public void getDisallowValueTest() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /abc");

        // act
		RobotDisallows disallows = sut.getDisallows(sb.toString());

        // assert
        Assert.assertEquals(1, disallows.getList().size());
        Assert.assertEquals("/abc", disallows.getList().get(0));

    }

    @Test
    public void get2AgentsWithNoLineSeparator() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: fooo");
        sb.appendLine("Disallow: /a");
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /b");

        // act
        RobotDisallows disallows = sut.getDisallows(sb.toString());

        // assert
        Assert.assertEquals(1, disallows.getList().size());
        Assert.assertEquals("/b", disallows.getList().get(0));

    }

    @Test
    public void hasTwoAgentsOnlyCareAbout2nd() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("User-agent: fooo");
        sb.appendLine("Disallow: /a");
        sb.appendLine("");
        sb.appendLine("User-agent: *");
        sb.appendLine("Disallow: /b");

        // act
        RobotDisallows disallows = sut.getDisallows(sb.toString());

        // assert
        Assert.assertEquals(1, disallows.getList().size());
        Assert.assertEquals("/b", disallows.getList().get(0));

    }

    @Test
    public void getZeroUserAgentsTest() {

         // arrange
        String content = "";

        // act
        RobotDisallows disallows = sut.getDisallows(content);

        // assert
        Assert.assertEquals(0, disallows.getList().size());
    }

    @Test
    public void getAgentsFromUnusualCasing() {

        // arrange
        StringBuilderWithNewline sb = new StringBuilderWithNewline();
        sb.appendLine("USER-AGENT: *");
        sb.appendLine("DISALLOW: /");

        // act
        RobotDisallows disallows = sut.getDisallows(sb.toString());

        // assert
        Assert.assertEquals(1, disallows.getList().size());

    }


}
