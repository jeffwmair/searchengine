package jwm.ir.service;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Db;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {


    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final Db db;
    public ServiceImpl(Db db) {
        AssertUtils.notNull(db, "Must provide a db instance");
        this.db = db;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        Page page = Page.create(url);
        Page parentPage = Page.create(parentUrl);
        PageLink pageLink = PageLink.create(parentPage, page);

        // send each entity to the db to be saved
        db.saveEach(page.getDomain(), page, parentPage.getDomain(), parentPage, pageLink);

    }
}
