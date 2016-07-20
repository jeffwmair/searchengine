package jwm.ir.utils;


/**
 * Created by Jeff on 2016-07-20.
 */
public class AssertUtils {
    public static void notNull(Object o, String msg) {
        if (o == null) throw new NullPointerException(msg);
    }

    public static void failState(boolean condition, String msg) {
        if (condition) throw new IllegalArgumentException(msg);
    }

    public static void notEmpty(String val, String msg) {
        notNull(val, msg);
        if (val.isEmpty()) throw new IllegalStateException(msg);
    }
}
