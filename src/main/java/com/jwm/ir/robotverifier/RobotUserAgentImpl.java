package com.jwm.ir.robotverifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user-agent identified in a robots.txt file, along
 * with any 'disallows' paths.
 * Created by Jeff on 2016-07-19.
 */
public class RobotUserAgentImpl implements RobotUserAgent{

    private final String agentName;
    private final List<String> disallowsList;
    public RobotUserAgentImpl(String agentName, List<String> disallowsList) {
        this.agentName = agentName;
        if (disallowsList == null) {
            this.disallowsList = new ArrayList<>();
        }
        else {
            this.disallowsList = disallowsList;
        }
    }

    @Override
    public String getAgentName() {
        return this.agentName;
    }

    @Override
    public List<String> getDisallows() {
        return disallowsList;
    }


}
