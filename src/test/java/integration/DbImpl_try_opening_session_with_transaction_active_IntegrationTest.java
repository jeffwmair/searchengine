package integration;

import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Jeff on 2016-07-31.
 */
public class DbImpl_try_opening_session_with_transaction_active_IntegrationTest {

    private Db sut;

    @Test
    public void test_try_openeing_session_when_already_open() {

        // this should pass
        sut.startTransaction();

        try {
            sut.startTransaction();
            Assert.fail("Should throw exception");
        }
        catch (IllegalStateException ex) { }
    }

    @Before
    public void setup() {
        sut = new DbImpl(HibernateUtil.getSessionFactory());
    }
}
