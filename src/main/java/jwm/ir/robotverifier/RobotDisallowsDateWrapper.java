package jwm.ir.robotverifier;

/**
 * Created by Jeff on 2016-07-20.
 */
public class RobotDisallowsDateWrapper {
    private final RobotDisallows disallows;
    private final long time;

    public RobotDisallowsDateWrapper(long time, RobotDisallows disallows) {
        this.time = time;
        this.disallows = disallows;
    }

    public long getTime() {
        return time;
    }

    public RobotDisallows getDisallows() {
        return disallows;
    }
}
