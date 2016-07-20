package jwm.ir.utils;

/**
 * Created by Jeff on 2016-07-19.
 */
public class StringBuilderWithNewline {

    private final StringBuilder sb;

    public StringBuilderWithNewline() {
        sb = new StringBuilder();
    }

    /**
     * Append a line with newline separator at the end
     * @param line
     */
    public void appendLine(String line) {
        sb.append(line + System.getProperty("line.separator"));
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
