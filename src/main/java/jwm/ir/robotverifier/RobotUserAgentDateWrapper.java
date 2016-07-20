package jwm.ir.robotverifier;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotUserAgentDateWrapper {
    private final RobotUserAgent agent;
    private final long time;

    public RobotUserAgentDateWrapper(long time, RobotUserAgent agent) {
        this.time = time;
        this.agent = agent;
    }

    public long getTime() {
        return time;
    }

    public RobotUserAgent getAgent() {
        return agent;
    }
}
