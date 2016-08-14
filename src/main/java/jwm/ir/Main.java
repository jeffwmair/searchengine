package jwm.ir;

import jwm.ir.entity.dao.DaoFactory;
import jwm.ir.indexer.StopwordsFileLoader;
import jwm.ir.main.TerminationWatcher;
import jwm.ir.message.WebResource;
import jwm.ir.service.Service;
import jwm.ir.service.ServiceImpl;
import jwm.ir.utils.HibernateUtil;
import jwm.ir.utils.IntegrationTestDataSetup;
import jwm.ir.workers.CrawlerWorker;
import jwm.ir.workers.IndexerWorker;
import jwm.ir.workers.RobotWorker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

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
		String host = "localhost";
		for(String arg : args) {
			if (arg.startsWith("--host=")) {
				host = arg.split("=")[1];
			}
			else if (arg.startsWith("--integration_test")) {
				// hack!
				IntegrationTestDataSetup.setup();
			}
			else if (arg.startsWith("--pagerank_interval=")) {
				prInterval = Integer.parseInt(arg.split("=")[1]);
			}
			else {
				log.error("Unknown argument: " + arg);
				log.error("Valid arguments include: --crawl={true/false} --index={true/false} --checkrobots={true/false} --pagerank_interval={int} --numworkers={int} --host={webservername}" + arg);
				System.exit(0);
			}
		}

		log.info("host=" + host);
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

}
