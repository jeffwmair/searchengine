package jwm.ir.utils;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.DomainRepositoryImpl;
import jwm.ir.domain.Page;
import jwm.ir.domain.ValidExtension;
import jwm.ir.service.UnitOfWork;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by Jeff on 2016-07-26.
 */
public class IntegrationTestDataSetup {
    final private static Logger log = LogManager.getLogger(IntegrationTestDataSetup.class);
    public static void setup() {
        log.info("Doing integration-test setup");
        setupPages();
        setupValidExtensions();
    }
    private static void setupValidExtensions() {
        String[] validExtesions = {
                "biz",
                "com",
                "edu",
                "gov",
                "info",
                "net",
                "org",
                "tv",
                "io",
                "at",
                "ca",
                "fr",
                "kr",
                "uk",
                "us",
                "it",
                "jp",
                "me",
                "mu",
                "no",
                "se"
        };

        for(String s : validExtesions) {
            saveExtension(s);
        }

    }

    private static void saveExtension(String ext) {
        int extensionTypeDefault = 1;
        ValidExtension validExtension = new ValidExtension(extensionTypeDefault, ext);
        log.info("Saving extension "+ext);
        getDb().save(validExtension);
    }

    private static void setupPages() {
        UnitOfWork unitOfWork = new UnitOfWork(HibernateUtil.getSessionFactory().openSession());
        DomainRepository domainRepository = new DomainRepositoryImpl(unitOfWork);
        Page page = Page.create("http://localhost/searchengine_test/page1.html", domainRepository);
        page.setVerified(1);
        log.info("Adding page to db:"+page);
        db.save(page.getDomain());
        db.save(page);
    }

    private static Db db = getDb();
    private static Db getDb() {
        if (db == null) {
            db = new DbImpl(HibernateUtil.getSessionFactory());
        }
        return db;
    }
}
