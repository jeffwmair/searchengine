package integration;

import jwm.ir.entity.Domain;
import jwm.ir.entity.Page;
import jwm.ir.entity.SummaryData;
import jwm.ir.entity.dao.DaoFactory;
import jwm.ir.service.ServiceImpl;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jeff on 2016-08-13.
 */
public class ServiceImpl_update_indexed_page_count_summaries_with_existing_summary_record_IntegrationTest extends DbTestBase {

    @Test
    public void update_indexed_page_count_summaries_with_existing_summary_record_IntegrationTest() {

        // put in 1 page with non-null PR, one page with null.  only non-null page-ranks are counted
        Domain d = createAndSaveDomain("google.com");
        createAndSavePage("google.com/a", d);
        Page p = createAndSavePage("google.com/b", d);
        p.setIsVerified();
        p.setPageRank(0.1);
        saveOrUpdate(p);

        ServiceImpl sut = new ServiceImpl(sessionFactory, new DaoFactory());
        sut.updateSummaries();

        // fetch the item
        Session session = sessionFactory.openSession();
        Object record = session.createCriteria(SummaryData.class)
                .add(Restrictions.eq("item", SummaryData.ItemIndexedPageCount))
                .uniqueResult();

        SummaryData summaryDataItem = (SummaryData)record;
        long indexedCount = summaryDataItem.value;

        // 1 page with page rank
        Assert.assertEquals(1, indexedCount);
    }
}
