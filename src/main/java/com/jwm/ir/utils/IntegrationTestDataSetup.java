package com.jwm.ir.utils;

import com.jwm.ir.entity.Page;
import com.jwm.ir.entity.ValidExtension;
import com.jwm.ir.entity.dao.DaoFactory;
import com.jwm.ir.service.Service;
import com.jwm.ir.service.ServiceImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(validExtension);
        tx.commit();
        session.close();
    }

    private static void setupPages() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Page page = new Page("http://localhost/searchengine_test/page1.html", Page.MakeNewDomain.Yes);
        page.setIsVerified();
        log.info("Adding page to db:"+page);
        Transaction tx = session.beginTransaction();
        session.save(page.getDomain());
        session.save(page);
        tx.commit();
        log.info("Committed save");
        session.close();
        log.info("closed session");
    }

    private static Service service = getService();
    private static Service getService() {
        if (service == null) {
            service = new ServiceImpl(HibernateUtil.getSessionFactory(), new DaoFactory());
        }
        return service;
    }
}
