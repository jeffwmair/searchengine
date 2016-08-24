package com.jwm.ir.robotverifier;

import java.util.List;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotUserAgentNone implements RobotUserAgent {
    @Override
    public String getAgentName() {
        throw new RuntimeException("Cannot get properties from RobotUserAgentNone");
    }

    @Override
    public List<String> getDisallows() {
        throw new RuntimeException("Cannot get properties from RobotUserAgentNone");
    }
}
