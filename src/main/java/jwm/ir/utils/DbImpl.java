package jwm.ir.utils;

import jwm.ir.domain.Page;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeff on 2016-07-25.
 */
public class DbImpl implements Db {

    private final Db phpDb;

    public DbImpl() {
        this.phpDb = new Database("localhost/searchengine");
    }

    @Override
    public void addPerformanceStats(int verifications, int crawls, int indexes) {
        phpDb.addPerformanceStats(verifications, crawls, indexes);
    }

    @Override
    public void updateSummaries() {
        phpDb.updateSummaries();
    }

    @Override
    public void setVerificationStatusForUrls(HashMap<String, Integer> urlVerificationResults) {
        phpDb.setVerificationStatusForUrls(urlVerificationResults);
    }

    @Override
    public List<String> getUnverifiedPagesForVerification() {
        return phpDb.getUnverifiedPagesForVerification();
    }


}
