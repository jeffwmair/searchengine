package com.jwm.ir;

import com.jwm.ir.index.StopwordsFileLoader;
import com.jwm.ir.index.resource.WebResource;
import com.jwm.ir.index.service.Service;
import com.jwm.ir.index.service.ServiceImpl;
import com.jwm.ir.index.workers.CrawlerWorker;
import com.jwm.ir.index.workers.IndexerWorker;
import com.jwm.ir.index.workers.RobotWorker;
import com.jwm.ir.persistence.HibernateUtil;
import com.jwm.ir.persistence.Page;
import com.jwm.ir.persistence.ValidExtension;
import com.jwm.ir.persistence.dao.DaoFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

	final private static Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {

		AtomicBoolean stopApplication = new AtomicBoolean(false);

		int prInterval = 500;
		for(String arg : args) {
			if (arg.startsWith("--integration_test")) {
				// hack!
				IntegrationTestDataSetup.setup();
			}
			else if (arg.startsWith("--pagerank_interval=")) {
				prInterval = Integer.parseInt(arg.split("=")[1]);
			}
			else {
				log.error("Unknown argument: " + arg);
				log.error("Valid arguments include: --pagerank_interval={int}" + arg);
				System.exit(0);
			}
		}

		log.info("pagerank_interval=" + Integer.toString(prInterval));

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Service service = new ServiceImpl(sessionFactory, new DaoFactory());

		List<String> domainExtensions = service.getValidDomainExtensions();
		if (log.isDebugEnabled()) {
			for (String s : domainExtensions) {
				log.debug("Valid jwm.ir.domain:" + s);
			}
		}

		// indexed file counter tells Indexer#1 when to run PageRank update
		AtomicInteger indexCounter = new AtomicInteger(0);

		BlockingQueue<WebResource> queue = new LinkedBlockingQueue<>();

		CrawlerWorker c1 = new CrawlerWorker(domainExtensions,
				service,
				queue,
				stopApplication);
		Thread t = new Thread(c1, "Crawler#");
		t.start();

		RobotWorker r = new RobotWorker(stopApplication, service);
		Thread robotThread = new Thread(r, "RobotWorker#");
		robotThread.start();

		StopwordsFileLoader stopwordsFileLoader = new StopwordsFileLoader("./stopwords.txt");
		IndexerWorker indexer = new IndexerWorker(queue,
				service,
				stopwordsFileLoader,
				indexCounter,
				prInterval,
				stopApplication);
		Thread indexerThread = new Thread(indexer, "IndexWorker#");
		indexerThread.start();

		TerminationWatcher terminationWatcher = new TerminationWatcher(stopApplication);
		Thread terminationWatcherThread = new Thread(terminationWatcher);
		terminationWatcherThread.start();

	}
	/**
	 * Created by Jeff on 2016-07-26.
	 */
	static class IntegrationTestDataSetup {
		final private static Logger log = LogManager.getLogger(IntegrationTestDataSetup.class);

		public static void setup() {
			log.info("Doing integration-test setup");
			setupPages();
			setupValidExtensions();
		}

		private static void setupValidExtensions() {
			String[] validExtesions = {
					"biz", "com", "edu", "gov", "info", "net",
					"org", "tv", "io", "at", "ca", "fr", "kr",
					"uk", "us", "it", "jp", "me", "mu", "no", "se"
			};

			for (String s : validExtesions) {
				saveExtension(s);
			}

		}

		private static void saveExtension(String ext) {
			int extensionTypeDefault = 1;
			ValidExtension validExtension = new ValidExtension(extensionTypeDefault, ext);
			log.info("Saving extension " + ext);
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
			log.info("Adding page to db:" + page);
			Transaction tx = session.beginTransaction();
			session.save(page.getDomain());
			session.save(page);
			tx.commit();
			session.close();
		}

		private static Service service = getService();

		private static Service getService() {
			if (service == null) service = new ServiceImpl(HibernateUtil.getSessionFactory(), new DaoFactory());
			return service;
		}
	}

	static class TerminationWatcher implements Runnable {
		final private static Logger log = LogManager.getLogger(TerminationWatcher.class);
		private AtomicBoolean stopApplication;

		public TerminationWatcher(AtomicBoolean stopApplication) {
			this.stopApplication = stopApplication;
		}

		@Override
		public void run() {

		/* this thread can check for a stop flag */
			File flagsDir = new File("./flags");
			if (!flagsDir.exists()) {
				flagsDir.mkdir();
			}

			while (true) {

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				File stopFlag = new File("./flags/stop.txt");
				if (stopFlag.exists()) {
					stopFlag.delete();
					log.info("Found flags/stop.txt,  so stopping the application");
					stopApplication.set(true);
					break;
				}
				else {
					log.debug("nothing to do....");
				}

			}
		}
	}
}

