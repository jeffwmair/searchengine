package jwm.ir.domain.persistence;

import jwm.ir.domain.Page;
import jwm.ir.domain.PageTerm;
import jwm.ir.domain.Term;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 * Created by Jeff on 2016-08-05.
 */
public class PageTermDaoImpl implements PageTermDao {
    private static final Logger log = LogManager.getLogger(PageTermDaoImpl.class);
    private final Session session;
    private final TermDao termDao;
    private final PageRepository pageRepository;
    public PageTermDaoImpl(TermDao termDao, PageRepository pageRepository, Session session) {
        this.session = session;
        this.termDao = termDao;
        this.pageRepository = pageRepository;
    }
    @Override
    public void create(long pageId, String termValue, int termFrequency) {
        log.info("pagetermDaoImpl.create pageId:'"+pageId+"', termValue:'"+termValue+"', termFrequency:'"+termFrequency+"'");
        Term term = termDao.createOrIncrementTermFrequency(termValue);
        Page page = pageRepository.getPage(pageId);
        PageTerm pt = new PageTerm(page, term, termFrequency);
        session.save(pt);
    }

    @Override
    public boolean termsAlreadyExist(long pageId) {
        Page page = pageRepository.getPage(pageId);
        return page.getPageTerms().size() > 0;
    }
}
