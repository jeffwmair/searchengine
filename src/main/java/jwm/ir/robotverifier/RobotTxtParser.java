package jwm.ir.robotverifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jeff on 2016-07-19.
 */
public class RobotTxtParser {

    private final List<String> lines;
    private final List<RobotUserAgent> agents;
    public RobotTxtParser(String robotsTxtContent) {

        this.lines = Arrays.asList(robotsTxtContent.split("\n"));
        this.agents = new ArrayList<>();
        List<String> disallowsList = null;

        String agentName = null;
        for (String line : lines) {

            if (line.toLowerCase().startsWith("user-agent:")) {
                if (agentName != null) {
                    storeAgent(agentName, disallowsList);
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
                    storeAgent(agentName, disallowsList);

                    // might be more agents, so reset our state variables
                    agentName = null;
                    disallowsList = null;
                }
            }
        }

        // final agent to close out
        if (agentName != null) {
            agents.add(new RobotUserAgent(agentName, disallowsList));
        }

    }

    private void storeAgent(String agentName, List<String> disallows) {
        this.agents.add(new RobotUserAgent(agentName, disallows));
    }

    private String splitValue(String line) {
        return line.split(":")[1].trim();
    }

    /**
     * Get list of agents
     * @return
     */
    public List<RobotUserAgent> parseAgents() {
        return agents;
    }
}
