package jwm.ir.indexer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jeff on 2016-08-14.
 */
public class StemmerWrapper {

    public static String stem(String term) {
        String trimmed = term.trim();

        if (shouldKeepSame(trimmed)) {
            return trimmed;
        }

        Stemmer stemmer = new Stemmer();
        stemmer.add(trimmed.toCharArray(), trimmed.length());
        stemmer.stem();
        return stemmer.toString();
    }

    private static Collection keep_same = new ArrayList();

    private static boolean shouldKeepSame(String val) {
        return keep_same.contains(val);
    }

    static {
        keep_same.add("this");
    }
}
