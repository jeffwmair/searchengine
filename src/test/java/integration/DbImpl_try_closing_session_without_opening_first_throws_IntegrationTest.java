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
public class DbImpl_try_closing_session_without_opening_first_throws_IntegrationTest {

    private Db sut;

    @Test
    public void test_try_closing_session_without_opening_first_throws() {
        try {
            sut.commitTransaction();
            Assert.fail("Should throw exception");
        }
        catch (IllegalStateException ex) { }
    }

    @Before
    public void setup() {
        sut = new DbImpl(HibernateUtil.getSessionFactory());
    }
}

