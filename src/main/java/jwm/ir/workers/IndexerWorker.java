package jwm.ir.workers;

import jwm.ir.indexer.CrawledTextParser;
import jwm.ir.indexer.IndexQueue;
import jwm.ir.indexer.TermPreprocessor;
import jwm.ir.utils.Database;
import jwm.ir.utils.JsonUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexerWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(IndexerWorker.class);
	private int _id;
	private File _dir;
	private ArrayList<String> _stopwords;
	private Database _db;
	private AtomicInteger _indexCount;
	private int _indexesBeforePrUpdate;
	private TermPreprocessor _tp;
	private AtomicBoolean _stopApp;
	private Thread _threadPageRankWorker;
	private Thread _updateStatsWorker;
	private PerformanceStatsUpdateWorker _perfWorker;
	
	public IndexerWorker(IndexQueue indexQueue,
						 File directory,
						 Database db,
						 ArrayList<String> stopwords,
						 PerformanceStatsUpdateWorker perfWorker,
						 int id,
						 AtomicInteger indexCount,
						 int indexesBeforePrUpdate,
						 TermPreprocessor tp,
						 AtomicBoolean stopApplication) {
		_id = id;
		_dir = directory;
		_stopwords = stopwords;
		_db = db;
		_indexCount = indexCount;
		_indexesBeforePrUpdate = indexesBeforePrUpdate;
		_tp = tp;
		_perfWorker = perfWorker;
		_stopApp = stopApplication;
	}
	
	@Override
	public void run() {
		log.info("Started");
		
		while (true && !_stopApp.get()) {
			
			/* have one indexer responsible for starting the PR thread */
			if (_indexCount.get() >= _indexesBeforePrUpdate && this._id == 1) {
				log.info("We have indexed " + _indexCount.get() + " files, now starting PageRankUpdater");
				startPageRankWorker();
				startSummarizerWorker();
				_indexCount.set(0);
			}
			
			File[] files = _dir.listFiles();
			for(File f : files) {
				processInputFile(f, _tp);
			}
			
			sleep(2);
		}
		
	}
	
	/**
	 * Starts PageRank updates
	 */
	private void startPageRankWorker() {
		if (_threadPageRankWorker == null || !_threadPageRankWorker.isAlive()) {
			PageRankCalculatorWorker worker = new PageRankCalculatorWorker(_db);
			_threadPageRankWorker = new Thread(worker);
			_threadPageRankWorker.start();
		}
		else {
			log.info("Not starting PageRank worker as it is already running");
		}
	}
	
	private void startSummarizerWorker() {
		log.info("Starting summarizer worker");
		if (_updateStatsWorker == null || !_updateStatsWorker.isAlive()) {
			DatabaseStatsUpdateWorker worker = new DatabaseStatsUpdateWorker(_db);
			_updateStatsWorker = new Thread(worker);
			_updateStatsWorker.start();
		}
		else {
			log.info("Not starting StatsUpdate worker as it is already running");
		}
	}
	
	/**
	 * Process an input crawled file
	 * @param file
	 */
	private void processInputFile(File file, TermPreprocessor tp) {
			
		if (!file.getName().startsWith("crawler" + Integer.toString(_id) + "_")) {
			return;
		}
	
		log.info("Beginning processing of " + file.getName());
		
		boolean useStemming = false;
		boolean useStopwords = true;
		CrawledTextParser parser = new CrawledTextParser(useStemming, useStopwords, _stopwords, tp);
		BufferedReader br = null;
		try
		{
			  
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"));
			String line;
			String urlLine = br.readLine();
			int pageId = _db.getPageIdFromUrl(urlLine);
			if (pageId < 1) {
				br.close();
				file.delete();
				return;
			}

			while ((line = br.readLine()) != null) {
				parser.processInput(line);
			} 
			br.close();
			log.info("Finished parsing the file, beginning to construct JSON");

			final String JSON_PAGE_ID = "p";
			final String JSON_TERMS = "ts";
			final String JSON_TERM = "t";
			final String JSON_TERM_FREQ = "tf";
			final int MAX_JSON_GET_LENGTH = 20000;
			
			StringBuilder json = new StringBuilder();
			json.append("{\""+ JSON_PAGE_ID +"\":\""+pageId+"\",\""+JSON_TERMS+"\":[");
			

			for(Map.Entry<String, Integer> e : parser.getTermFrequencies().entrySet()) {
				
				String term = e.getKey();
				String tf = Integer.toString(e.getValue());
				
				if (!json.toString().endsWith(":[")) json.append(",");
				
				json.append("{");
				json.append(JsonUtils.getJsonItem(JSON_TERM, term) + ",");
				json.append(JsonUtils.getJsonItem(JSON_TERM_FREQ, tf));
				json.append("}");			
				
				if (json.length() > MAX_JSON_GET_LENGTH) {

					// close off the json
					json.append("]}");
					
					// send it
					long start = System.currentTimeMillis();
					_db.addDocumentTerms(json.toString(), pageId);
					log.info("Sent an intermediate batch of JSON: " + (System.currentTimeMillis() - start) + "ms");
					
					json = new StringBuilder();
					json.append("{\""+JSON_PAGE_ID+"\":\""+pageId+"\",\""+JSON_TERMS+"\":[");
				}
			}
			if (json.length() > 0) {
				json.append("]}");
				
				// send the last of it
				long start = System.currentTimeMillis();
				_db.addDocumentTerms(json.toString(), pageId);
				log.info("Sent the last batch of JSON: " + (System.currentTimeMillis() - start) + "ms");
			}
			
			// delete when done processing
			file.delete();
			_perfWorker.incrementPagesIndexed();
		}
		catch(Exception ex) {
			log.error("Error parsing content from " + file.getName());
			moveFileToErrorFolder(file);
		}
		finally{
			_indexCount.incrementAndGet();
		}
		

		
	}
	
	private void moveFileToErrorFolder(File f) {
		f.renameTo(new File(f.getAbsolutePath().replace("crawler", "error")));
	}
	
	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}