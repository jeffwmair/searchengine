package integration;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbSetupForSystemTest {

    public static void main(String[] args) {

        /*
        Create test data; that is all.
         */
        Domain domain = new Domain("localhost/searchengine_test", 1);
        Page page = new Page(domain, "http://localhost/searchengine_test/page1.html");
        Db db = new DbImpl(HibernateUtil.getSessionFactory());
        db.save(page);

    }
}
