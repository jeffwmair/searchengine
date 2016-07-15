package jwm.ir.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jwm.ir.indexerutils.TermPreprocessor;
import jwm.ir.utils.Database;
import jwm.ir.utils.Log;
import jwm.ir.workers.CrawlerWorker;
import jwm.ir.workers.IndexerWorker;
import jwm.ir.workers.PageRankCalculatorWorker;
import jwm.ir.workers.PerformanceStatsUpdateWorker;
import jwm.ir.workers.RobotWorker;


public class IrMain {

	private static final String LOG_NAME = "IRSYSTEM-MAIN";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		start(args);
//		test();
	}
	
	private static void start(String[] args) {
		
		AtomicBoolean stopApplication = new AtomicBoolean(false);
				
		Log log = new Log();
		File documentDir = new File("toindex");
		if (!documentDir.exists()) {
			documentDir.mkdir();
		}
		
		int workers = 1;
		int prInterval = 500;
		boolean runCrawlers = false;
		boolean runRobotChecker = false;
		boolean runIndexers = false;
		String host = "localhost/searchengine";
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
			else if (arg.startsWith("--printerval=")) {
				prInterval = Integer.parseInt(arg.split("=")[1]);
			}
			else {
				log.LogMessage(LOG_NAME, "Unknown argument: " + arg, true);
				log.LogMessage(LOG_NAME, "Valid arguments include: --crawl={true/false} --index={true/false} --checkrobots={true/false} --printerval={int} --numworkers={int} --host={webservername}" + arg, true);
				System.exit(0);
			}
		}
		
		log.LogMessage(LOG_NAME, "crawl=" + Boolean.toString(runCrawlers), false);
		log.LogMessage(LOG_NAME, "checkrobots=" + Boolean.toString(runRobotChecker), false);
		log.LogMessage(LOG_NAME, "index=" + Boolean.toString(runIndexers), false);
		log.LogMessage(LOG_NAME, "numworkers=" + Integer.toString(workers), false);
		log.LogMessage(LOG_NAME, "host=" + host, false);
		log.LogMessage(LOG_NAME, "printerval=" + Integer.toString(prInterval), false);
	
		
		Database db = new Database(host, log);
		
		ArrayList<String> validPageExtensions = new ArrayList<String>();
		ArrayList<String> validDomainExtensions = new ArrayList<String>();
		db.getValidExtensions(LOG_NAME, validPageExtensions, validDomainExtensions, log);
				
		// indexed file counter tells Indexer#1 when to run PageRank update
		AtomicInteger indexCounter = new AtomicInteger(0);
		
		PerformanceStatsUpdateWorker performanceWorker = new PerformanceStatsUpdateWorker(db, log, workers, stopApplication);
				
		for(int i = 1; i <= workers; i++) {
			
			String num = Integer.toString(i);
			
			if (runCrawlers) {
				
				CrawlerWorker c1 = new CrawlerWorker(i, validPageExtensions, validDomainExtensions, log, db, documentDir, runIndexers, performanceWorker, stopApplication);
				Thread t = new Thread(c1, "Crawler#" + num);
				t.start();
			}
			
			if (runRobotChecker) {
				
				RobotWorker r = new RobotWorker(i, stopApplication, performanceWorker, log, db);
				Thread robotThread = new Thread(r, "RobotWorker#" + num);
				robotThread.start();
				
			}

			if (runIndexers) {
			
				IndexerWorker indexer = new IndexerWorker(documentDir, 
						db, 
						getStopwordsFromFile(log), 
						performanceWorker,
						log, 
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
			File stopFlag = new File("./flags/stop.txt");
			if (stopFlag.exists()) {
				stopFlag.delete();
				log.LogMessage(LOG_NAME, "Found flags/stop.txt,  so stopping the application", false);
				stopApplication.set(true);
				break;				
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private static ArrayList<String> getStopwordsFromFile(Log l) {
		String fname = "./stopwords.txt";
		File inputFile = new File(fname);
		if (!inputFile.exists()) {
			l.LogMessage(LOG_NAME, "Could not find stopwords file", true);
			return new ArrayList<String>();
		}
		
		BufferedReader br = null;
		ArrayList<String> stopwords = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fname));
			String line;
			while ((line = br.readLine()) != null) {
				stopwords.add(line.toLowerCase());
			} 
			br.close();
			return stopwords;	
		} catch (Exception e) {
			l.LogMessage(LOG_NAME, "Error loading stopwords file", true);
			return new ArrayList<String>();			
		}
	}
	
	private static void testPr(Database _db, Log _log) {
		PageRankCalculatorWorker worker = new PageRankCalculatorWorker(_db, _log);
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
