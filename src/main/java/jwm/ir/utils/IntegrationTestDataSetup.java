package jwm.ir.utils;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by Jeff on 2016-07-26.
 */
public class IntegrationTestDataSetup {
    final private static Logger log = LogManager.getLogger(IntegrationTestDataSetup.class);
    public static boolean setup(String[] args) {

        boolean doSetup = false;
        for (String s : args) {
            if (s.contains("integration_test")) {
                doSetup = true;
                break;
            }
        }

        if (!doSetup) return false;

        Domain domain = new Domain("localhost/searchengine_test", 1);
        Page page = new Page(domain, "http://localhost/searchengine_test/page1.html");
        Db db = new DbImpl(HibernateUtil.getSessionFactory());
        log.info("Adding page to db:"+page);
        db.save(page);
        log.info("Page saved.");
        return true;
    }
}
