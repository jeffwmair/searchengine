package jwm.ir.main;

import jwm.ir.indexer.TermPreprocessor;
import jwm.ir.message.WebResource;
import jwm.ir.utils.Database;
import jwm.ir.utils.Db;
import jwm.ir.utils.HibernateUtil;
import jwm.ir.utils.IntegrationTestDataSetup;
import jwm.ir.workers.CrawlerWorker;
import jwm.ir.workers.IndexerWorker;
import jwm.ir.workers.PerformanceStatsUpdateWorker;
import jwm.ir.workers.RobotWorker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class IrMain {

	final private static Logger log = LogManager.getLogger(IrMain.class);

	public static void main(String[] args) {

		AtomicBoolean stopApplication = new AtomicBoolean(false);
		HibernateUtil.getSessionFactory();

		int prInterval = 500;
		boolean runCrawlers = false;
		boolean runRobotChecker = false;
		boolean runIndexers = false;
		String host = "localhost";
		for(String arg : args) {
			if (arg.startsWith("--crawl")) {
				runCrawlers = Boolean.parseBoolean(arg.split("=")[1]);
			}
			else if (arg.startsWith("--checkrobots")) {
				runRobotChecker = Boolean.parseBoolean(arg.split("=")[1]);
			}
			else if (arg.startsWith("--index")) {
				runIndexers = Boolean.parseBoolean(arg.split("=")[1]);
			}
			else if (arg.startsWith("--host=")) {
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

		log.info("crawl=" + Boolean.toString(runCrawlers));
		log.info("checkrobots=" + Boolean.toString(runRobotChecker));
		log.info("index=" + Boolean.toString(runIndexers));
		log.info("host=" + host);
		log.info("pagerank_interval=" + Integer.toString(prInterval));


		Db db = new Database(host);

		List<String> domainExtensions = db.getValidDomainExtensions();
		if (log.isDebugEnabled()) {
			for (String s : domainExtensions) {
				log.debug("Valid domain:" + s);
			}
		}

		// indexed file counter tells Indexer#1 when to run PageRank update
		AtomicInteger indexCounter = new AtomicInteger(0);

		PerformanceStatsUpdateWorker performanceWorker = new PerformanceStatsUpdateWorker(db, stopApplication);
		BlockingQueue<WebResource> queue = new LinkedBlockingQueue<>();


		if (runCrawlers) {

			CrawlerWorker c1 = new CrawlerWorker(domainExtensions,
					db,
					queue,
					runIndexers,
					performanceWorker,
					stopApplication);
			Thread t = new Thread(c1, "Crawler#");
			t.start();
		}

		if (runRobotChecker) {

			RobotWorker r = new RobotWorker(stopApplication, performanceWorker, db);
			Thread robotThread = new Thread(r, "RobotWorker#");
			robotThread.start();

		}

		if (runIndexers) {

			IndexerWorker indexer = new IndexerWorker(queue,
					db,
					getStopwordsFromFile(),
					performanceWorker,
					indexCounter,
					prInterval,
					getTermProcessor(),
					stopApplication);
			Thread indexerThread = new Thread(indexer, "IndexWorker#");
			indexerThread.start();

		}

		/* start the performance worker */
		Thread perfWorkerThread = new Thread(performanceWorker);
		perfWorkerThread.start();
		
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

		}
	}

	private static TermPreprocessor getTermProcessor() {

		ArrayList<String> toReplace = new ArrayList<String>();
		toReplace.add("$");
		toReplace.add("@");
		toReplace.add("\"");
		toReplace.add("/");
		toReplace.add(":");
		toReplace.add("#");
		toReplace.add("%");
		toReplace.add("_");
		toReplace.add(".");
		toReplace.add(",");
		toReplace.add(";");
		toReplace.add("(");
		toReplace.add(")");
		toReplace.add("'");
		toReplace.add("*");
		toReplace.add("+");
		toReplace.add("-");
		toReplace.add(">");
		toReplace.add("<");
		toReplace.add("!");
		toReplace.add("&");
		toReplace.add("=");
		toReplace.add("[");
		toReplace.add("]");
		toReplace.add("`");
		toReplace.add("~");
		toReplace.add("?");
		toReplace.add("|");
		toReplace.add("{");
		toReplace.add("}");
		TermPreprocessor tp = new TermPreprocessor(toReplace);
		return tp;
	}

	private static ArrayList<String> getStopwordsFromFile() {
		String fname = "./stopwords.txt";
		File inputFile = new File(fname);
		if (!inputFile.exists()) {
			log.error("Could not find stopwords file");
			return new ArrayList<String>();
		}

		BufferedReader br = null;
		ArrayList<String> stopwords = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(fname));
			String line;
			while ((line = br.readLine()) != null) {
				stopwords.add(line.toLowerCase());
			}
			br.close();
			return stopwords;
		} catch (Exception e) {
			log.error("Error loading stopwords file");
			return new ArrayList<String>();
		}
	}

}
