package jwm.ir.workers;

import jwm.ir.indexer.CrawledTextParser;
import jwm.ir.indexer.StopwordsFileLoader;
import jwm.ir.indexer.TermPreprocessor;
import jwm.ir.message.WebResource;
import jwm.ir.message.WebResourceNoneImpl;
import jwm.ir.service.Service;
import jwm.ir.utils.Db;
import jwm.ir.utils.JsonUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexerWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(IndexerWorker.class);
	private final List<String> _stopwords;
	private final Db _db;
	private final AtomicInteger _indexCount;
	private final int _indexesBeforePrUpdate;
	private final TermPreprocessor _tp;
	private final AtomicBoolean _stopApp;
	private Thread _threadPageRankWorker;
	private Thread _updateStatsWorker;
	private final PerformanceStatsUpdateWorker _perfWorker;
	private final BlockingQueue<WebResource> indexQueue;
	private final Service service;
	
	public IndexerWorker(BlockingQueue<WebResource> indexQueue,
						 Db db,
						 Service service,
						 StopwordsFileLoader stopwordsFileLoader,
						 PerformanceStatsUpdateWorker perfWorker,
						 AtomicInteger indexCount,
						 int indexesBeforePrUpdate,
						 AtomicBoolean stopApplication) {

		if (indexQueue == null) throw new RuntimeException("Must provide non-null indexQueue");
		this.indexQueue = indexQueue;
		this.service = service;
		_stopwords = stopwordsFileLoader.getStopwordsFromFile();
		_db = db;
		_indexCount = indexCount;
		_indexesBeforePrUpdate = indexesBeforePrUpdate;
		_tp = getTermProcessor();
		_perfWorker = perfWorker;
		_stopApp = stopApplication;
	}
		private static TermPreprocessor getTermProcessor() {

		ArrayList<String> toReplace = new ArrayList<>();
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
	
	@Override
	public void run() {
		log.info("Started");
		
		while (true && !_stopApp.get()) {
			
			/* have one indexer responsible for starting the PR thread */
			if (_indexCount.get() >= _indexesBeforePrUpdate) {
				log.info("We have indexed " + _indexCount.get() + " files, now starting PageRankUpdater");
				startPageRankWorker();
				startSummarizerWorker();
				_indexCount.set(0);
			}

			WebResource parsedWebPage = null;
			try {
				parsedWebPage = indexQueue.take();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.interrupted();
				return;
			}
			log.info("Received new page to process:"+parsedWebPage.getUrl());
			processInputFile(parsedWebPage, _tp);

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
	 */
	private void processInputFile(WebResource page, TermPreprocessor tp) {

		if (page instanceof WebResourceNoneImpl) {
			// this is not a page, its a null object, so ignore
			return;
		}

		log.info("Beginning processing of " + page.getUrl());
		int pageId = _db.getPageIdFromUrl(page.getUrl());
		if (pageId < 1) {
			// ignore this page because the crawler didn't create a db record for it
			return;
		}

		
		boolean useStemming = false;
		boolean useStopwords = true;
		CrawledTextParser parser = new CrawledTextParser(useStemming, useStopwords, _stopwords, tp);
		parser.processInput(page.getContent());
		log.info("Finished parsing the file, beginning to construct JSON");
		final String JSON_PAGE_ID = "p";
		final String JSON_TERMS = "ts";
		final String JSON_TERM = "t";
		final String JSON_TERM_FREQ = "tf";
		final int MAX_JSON_GET_LENGTH = 20000;

		StringBuilder json = new StringBuilder();
		json.append("{\""+ JSON_PAGE_ID +"\":\""+pageId+"\",\""+JSON_TERMS+"\":[");

		boolean useNewCode = true;

		if (useNewCode) {
			log.warn("Using new service.addDocumentTerms!");
			service.addDocumentTerms(pageId, parser.getTermFrequencies());
		}
		else {
			log.warn("Using old deprecated php service");
			for (Map.Entry<String, Integer> e : parser.getTermFrequencies().entrySet()) {

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
					json.append("{\"" + JSON_PAGE_ID + "\":\"" + pageId + "\",\"" + JSON_TERMS + "\":[");
				}
			}
			if (json.length() > 0) {
				json.append("]}");

				// send the last of it
				long start = System.currentTimeMillis();
				_db.addDocumentTerms(json.toString(), pageId);
				log.info("Sent the last batch of JSON: " + (System.currentTimeMillis() - start) + "ms");
			}
		}

		// delete when done processing
		_perfWorker.incrementPagesIndexed();
		_indexCount.incrementAndGet();


		
	}

}