package jwm.ir.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jwm.ir.indexerutils.CrawledTextParser;
import jwm.ir.indexerutils.TermPreprocessor;
import jwm.ir.utils.Database;
import jwm.ir.utils.JsonUtils;
import jwm.ir.utils.Log;

public class IndexerWorker implements Runnable {
	
	private int _id;
	private Log _log;
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
	
	public IndexerWorker(File directory, Database db, ArrayList<String> stopwords, PerformanceStatsUpdateWorker perfWorker, Log l, int id, 
			AtomicInteger indexCount, 
			int indexesBeforePrUpdate,
			TermPreprocessor tp,
			AtomicBoolean stopApplication) {
		_id = id;
		_log = l;
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
		log("Started", false);
		
		while (true && !_stopApp.get()) {
			
			/* have one indexer responsible for starting the PR thread */
			if (_indexCount.get() >= _indexesBeforePrUpdate && this._id == 1) {
				log("We have indexed " + _indexCount.get() + " files, now starting PageRankUpdater", false);
				startPageRankWorker();
				startSummarizerWorker();
				_indexCount.set(0);
			}
			
			File[] files = _dir.listFiles();
			for(File f : files) {
				processInputFile(f, _tp);
			}
			
			sleep(10);
		}
		
	}
	
	/**
	 * Starts PageRank updates
	 */
	private void startPageRankWorker() {
		if (_threadPageRankWorker == null || !_threadPageRankWorker.isAlive()) {
			PageRankCalculatorWorker worker = new PageRankCalculatorWorker(_db, _log);
			_threadPageRankWorker = new Thread(worker);
			_threadPageRankWorker.start();
		}
		else {
			log("Not starting PageRank worker as it is already running", false);
		}
	}
	
	private void startSummarizerWorker() {
		if (_updateStatsWorker == null || !_updateStatsWorker.isAlive()) {
			DatabaseStatsUpdateWorker worker = new DatabaseStatsUpdateWorker(_db, _log);
			_updateStatsWorker = new Thread(worker);
			_updateStatsWorker.start();
		}
		else {
			log("Not starting StatsUpdate worker as it is already running", false);
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
	
		log("Beginning processing of " + file.getName(), false);
		
		boolean useStemming = false;
		boolean useStopwords = true;
		CrawledTextParser parser = new CrawledTextParser(useStemming, useStopwords, _stopwords, tp, _log);
		BufferedReader br = null;
		try
		{
			  
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"));
			String line;
			String urlLine = br.readLine();
			int pageId = _db.getPageIdFromUrl(getClientName(), urlLine);
			if (pageId < 1) {
				br.close();
				file.delete();
				return;
			}

			while ((line = br.readLine()) != null) {
				parser.processInput(line);
			} 
			br.close();
			log("Finished parsing the file, beginning to construct JSON", false);

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
					_db.addDocumentTerms(getClientName(), json.toString(), pageId);
					log("Sent an intermediate batch of JSON: " + (System.currentTimeMillis() - start) + "ms", false);
					
					json = new StringBuilder();
					json.append("{\""+JSON_PAGE_ID+"\":\""+pageId+"\",\""+JSON_TERMS+"\":[");
				}
			}
			if (json.length() > 0) {
				json.append("]}");
				
				// send the last of it
				long start = System.currentTimeMillis();
				_db.addDocumentTerms(getClientName(), json.toString(), pageId);
				log("Sent the last batch of JSON: " + (System.currentTimeMillis() - start) + "ms", false);
			}
			
			// delete when done processing
			file.delete();
			_perfWorker.incrementPagesIndexed();
		}
		catch(Exception ex) {
			_log.LogMessage(getClientName(), "Error parsing content from " + file.getName(), true);
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
	private void log(String msg, boolean error) {
		_log.LogMessage(getClientName(), msg, error);
	}
	private String getClientName() { return "IndexerWorker" + Integer.toString(_id); }

}