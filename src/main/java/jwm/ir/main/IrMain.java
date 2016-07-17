package jwm.ir.main;

import jwm.ir.indexerutils.TermPreprocessor;
import jwm.ir.utils.Database;
import jwm.ir.workers.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class IrMain {

	final private static Logger log = LogManager.getLogger(IrMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		start(args);
//		test();
	}
	
	private static void start(String[] args) {
		
		AtomicBoolean stopApplication = new AtomicBoolean(false);
				
		File documentDir = new File("toindex");
		if (!documentDir.exists()) {
			documentDir.mkdir();
		}
		
		int workers = 1;
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
			else if (arg.startsWith("--numworkers=")) {
				workers = Integer.parseInt(arg.split("=")[1]);
			}
			else if (arg.startsWith("--host=")) {
				host = arg.split("=")[1];
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
		log.info("numworkers=" + Integer.toString(workers));
		log.info("host=" + host);
		log.info("pagerank_interval=" + Integer.toString(prInterval));

		
		Database db = new Database(host);
		
		ArrayList<String> validPageExtensions = new ArrayList<>();
		ArrayList<String> validDomainExtensions = new ArrayList<>();
		db.getValidExtensions(validPageExtensions, validDomainExtensions);
				
		// indexed file counter tells Indexer#1 when to run PageRank update
		AtomicInteger indexCounter = new AtomicInteger(0);
		
		PerformanceStatsUpdateWorker performanceWorker = new PerformanceStatsUpdateWorker(db, workers, stopApplication);
				
		for(int i = 1; i <= workers; i++) {
			
			String num = Integer.toString(i);
			
			if (runCrawlers) {
				
				CrawlerWorker c1 = new CrawlerWorker(i, validPageExtensions, validDomainExtensions, db, documentDir, runIndexers, performanceWorker, stopApplication);
				Thread t = new Thread(c1, "Crawler#" + num);
				t.start();
			}
			
			if (runRobotChecker) {
				
				RobotWorker r = new RobotWorker(i, stopApplication, performanceWorker, db);
				Thread robotThread = new Thread(r, "RobotWorker#" + num);
				robotThread.start();
				
			}

			if (runIndexers) {
			
				IndexerWorker indexer = new IndexerWorker(documentDir, 
						db, 
						getStopwordsFromFile(),
						performanceWorker,
						i,
						indexCounter, 
						prInterval,
						getTermProcessor(),
						stopApplication);
				Thread indexerThread = new Thread(indexer, "IndexWorker#" + num);
				indexerThread.start();
				
			}
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
	
	private static void testPr(Database _db) {
		PageRankCalculatorWorker worker = new PageRankCalculatorWorker(_db);
		Thread t = new Thread(worker);
		t.start();
	}
	
	private static void test(File documentDir) {
		File[] files = documentDir.listFiles();
		int x =0; 
		for(File f : files) {
			if (x % 3 ==0) {
				if (f.getName().contains("crawler1")){
					f.renameTo(new File(f.getAbsolutePath().replace("crawler1", "crawler2")));
				}
			}
			if (x % 4 ==0) {
				if (f.getName().contains("crawler1")){
					f.renameTo(new File(f.getAbsolutePath().replace("crawler1", "crawler3")));
				}
			}
			if (x % 5 ==0) {
				if (f.getName().contains("crawler1")){
					f.renameTo(new File(f.getAbsolutePath().replace("crawler1", "crawler4")));
				}
			}
			x++;
		}
	}
}
