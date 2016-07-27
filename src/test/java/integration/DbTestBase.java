package integration;

import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.Before;

/**
 * Created by Jeff on 2016-07-26.
 */
public class DbTestBase {

    protected SessionFactory sessionFactory;
    protected Db db;

    @Before
    public void setup() {
        sessionFactory = HibernateUtil.getSessionFactory();
        db = new DbImpl(sessionFactory);
    }
}
