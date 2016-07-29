package jwm.ir.service;

import jwm.ir.domain.Domain;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLink;
import jwm.ir.utils.AssertUtils;
import jwm.ir.utils.Db;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {

    private final Db db;
    public ServiceImpl(Db db) {
        AssertUtils.notNull(db, "Must provide a db instance");
        this.db = db;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        // todo: this needs to be changed to be transactional

        Page page = Page.create(url);
        Page parentPage = Page.create(parentUrl);
        PageLink pageLink = PageLink.create(parentPage, page);
        db.save(page.getDomain());
        db.save(page);
        db.save(parentPage.getDomain());
        db.save(parentPage);
        db.save(pageLink);

    }
}
