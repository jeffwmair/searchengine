package jwm.ir.service;

import jwm.ir.crawler.UrlUtils;
import jwm.ir.domain.*;
import jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Map;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImpl implements Service {

    private static final Logger log = LogManager.getLogger(ServiceImpl.class);
    private final SessionFactory sessionFactory;
    private final RepositoryFactory repositoryFactory;
    public ServiceImpl(SessionFactory sessionFactory,
                       RepositoryFactory repositoryFactory) {
        AssertUtils.notNull(sessionFactory, "Must provide sessionFactory");
        this.sessionFactory = sessionFactory;
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public void addUrlForCrawling(String url, String parentUrl) {

        if (log.isDebugEnabled()) {
            log.debug("Beginning to add url for crawling:"+url);
        }


        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();


        DomainRepository domainRepository = repositoryFactory.createDomainRepository(session);
        PageRepository pageRepository = repositoryFactory.createPageRepository(session);
        PageLinkRepository pageLinkRepository = repositoryFactory.createPageLinkRepository(session);

        // only run if the page doesn't exist
        if (pageRepository.pageExists(url)) {
            log.warn("Page with url '"+url+"' already exists in the database, so not adding");
            tx.rollback();
            return;
        }

        Page page = pageRepository.create(url, domainRepository);
        Domain pageDomain;
        final String pageDomainName = UrlUtils.getDomainFromAbsoluteUrl(url);
        if (domainRepository.domainExists(pageDomainName)) {
            pageDomain = domainRepository.getDomain(pageDomainName);
        }
        else {
            pageDomain = domainRepository.create(pageDomainName);
        }

        page.setDomain(pageDomain);

        // we assume that the parent page must have already been indexed; how else can it be the parent?
        Page referantPage = pageRepository.getPage(parentUrl);

        pageLinkRepository.create(page, referantPage);

        tx.commit();
        session.close();

        if (log.isDebugEnabled()) {
            log.debug("Completed to add url for crawling:" + url);
        }

    }

    @Override
    public void addDocumentTerms(long pageId, Map<String, Integer> termFrequences) {

        log.warn("Using old deprecated php service");
        for (Map.Entry<String, Integer> e : termFrequences.entrySet()) {

            String term = e.getKey();
            String tf = Integer.toString(e.getValue());

            //if (!json.toString().endsWith(":[")) json.append(",");

//            json.append("{");
//            json.append(JsonUtils.getJsonItem(JSON_TERM, term) + ",");
//            json.append(JsonUtils.getJsonItem(JSON_TERM_FREQ, tf));
//            json.append("}");

//            if (json.length() > MAX_JSON_GET_LENGTH) {

                // close off the json
//                json.append("]}");

                // send it
//                long start = System.currentTimeMillis();
//                _db.addDocumentTerms(json.toString(), pageId);
//                log.info("Sent an intermediate batch of JSON: " + (System.currentTimeMillis() - start) + "ms");

//                json = new StringBuilder();
//                json.append("{\"" + JSON_PAGE_ID + "\":\"" + pageId + "\",\"" + JSON_TERMS + "\":[");
//            }
        }
//        if (json.length() > 0) {
//            json.append("]}");
//
//             send the last of it
//            long start = System.currentTimeMillis();
//            _db.addDocumentTerms(json.toString(), pageId);
//            log.info("Sent the last batch of JSON: " + (System.currentTimeMillis() - start) + "ms");
//        }
    }

}
