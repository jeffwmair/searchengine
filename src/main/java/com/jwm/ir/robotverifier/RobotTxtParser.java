package com.jwm.ir.robotverifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for parsing the content of a robots.txt file.
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParser {

    private void storeAgent(List<RobotUserAgent> agents, String agentName, List<String> disallows) {
        agents.add(new RobotUserAgentImpl(agentName, disallows));
    }

    private String splitValue(String line) {
        return line.split(":")[1].trim();
    }

	public RobotDisallows getDisallows(String robotsTxtContent) {
		List<RobotUserAgent> agents = parseAgents(robotsTxtContent);
		List<String> disallows = new ArrayList<>();
		for( RobotUserAgent agent : agents ) {
			if (agent.getAgentName().equals("*")) {
               return new RobotDisallows(agent.getDisallows());
			}
		}

        return new RobotDisallows(new ArrayList<String>());

	}


    /**
     * Get list of agents
     * @return
     */
    private List<RobotUserAgent> parseAgents(String robotsTxtContent) {

        List<String> lines = Arrays.asList(robotsTxtContent.split("\n"));
        List<RobotUserAgent> agents = new ArrayList<>();
        List<String> disallowsList = null;

        String agentName = null;
        for (String line : lines) {

            if (line.toLowerCase().startsWith("user-agent:")) {
                if (agentName != null) {
                    storeAgent(agents, agentName, disallowsList);
                    disallowsList = null;
                }
                agentName = splitValue(line);
            }
            else if (line.toLowerCase().startsWith("disallow:")){
                if (disallowsList == null) {
                    disallowsList = new ArrayList<>();
                }
                disallowsList.add(splitValue(line));
            }
            else {
                // must be end of the agent
                if (agentName != null) {
                    storeAgent(agents, agentName, disallowsList);

                    // might be more agents, so reset our state variables
                    agentName = null;
                    disallowsList = null;
                }
            }
        }

        // final agent to close out
        if (agentName != null) {
            agents.add(new RobotUserAgentImpl(agentName, disallowsList));
        }

        return agents;
    }

}
