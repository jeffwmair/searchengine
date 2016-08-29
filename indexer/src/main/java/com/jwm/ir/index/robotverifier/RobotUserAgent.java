package com.jwm.ir.index.robotverifier;

import java.util.List;

/**
 * Created by Jeff on 2016-07-20.
 */
public interface RobotUserAgent {
    String getAgentName();
    List<String> getDisallows();
}
