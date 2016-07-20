package jwm.ir.robotverifier;

import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Clock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for parsing the content of a robots.txt file.
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParser {

    private final Clock clock;
    public RobotTxtParser(Clock clock) {

        AssertUtils.notNull(clock, "provide a clock!");
        this.clock = clock;


    }

    private void storeAgent(List<RobotUserAgentImpl> agents, String agentName, List<String> disallows) {
        agents.add(new RobotUserAgentImpl(agentName, disallows));
    }

    private String splitValue(String line) {
        return line.split(":")[1].trim();
    }

    /**
     * Get list of agents
     * @return
     */
    public List<RobotUserAgentImpl> parseAgents(String robotsTxtContent) {

        List<String> lines = Arrays.asList(robotsTxtContent.split("\n"));
        List<RobotUserAgentImpl> agents = new ArrayList<>();
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
