package jwm.ir.workers;

import jwm.ir.indexer.CrawledTextParser;
import jwm.ir.indexer.StopwordsFileLoader;
import jwm.ir.indexer.TermPreprocessor;
import jwm.ir.message.WebResource;
import jwm.ir.message.WebResourceNoneImpl;
import jwm.ir.service.Service;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexerWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(IndexerWorker.class);
	private final List<String> _stopwords;
	private final AtomicInteger _indexCount;
	private final int _indexesBeforePrUpdate;
	private final TermPreprocessor _tp;
	private final AtomicBoolean _stopApp;
	private final BlockingQueue<WebResource> indexQueue;
	private final Service service;

	public IndexerWorker(BlockingQueue<WebResource> indexQueue,
						 Service service,
						 StopwordsFileLoader stopwordsFileLoader,
						 AtomicInteger indexCount,
						 int indexesBeforePrUpdate,
						 AtomicBoolean stopApplication) {

		if (indexQueue == null) throw new RuntimeException("Must provide non-null indexQueue");
		this.indexQueue = indexQueue;
		this.service = service;
		_stopwords = stopwordsFileLoader.getStopwordsFromFile();
		_indexCount = indexCount;
		_indexesBeforePrUpdate = indexesBeforePrUpdate;
		_tp = getTermProcessor();
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

		log.info("Started indexer.");

		while (true && !_stopApp.get()) {
			
			/* have one indexer responsible for starting the PR thread */
			if (_indexCount.get() >= _indexesBeforePrUpdate) {
				log.info("We have indexed " + _indexCount.get() + " files, now starting PageRankUpdater");
				//startPageRankWorker();
				//startSummarizerWorker();
				_indexCount.set(0);
			}

			WebResource parsedWebPage = null;
			try {
				log.info("waiting on queue");
				parsedWebPage = indexQueue.take();
				log.info("rec'd from queue new page to process:"+parsedWebPage.getUrl());
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.interrupted();
				return;
			}

			try {
				processInputFile(parsedWebPage, _tp);
			}
			catch(Exception ex) {
				log.error(ex.getMessage(), ex);
			}

		}

	}

	/**
	 * Starts PageRank updates
	private void startPageRankWorker() {
		if (_threadPageRankWorker == null || !_threadPageRankWorker.isAlive()) {
			PageRankCalculatorWorker worker = new PageRankCalculatorWorker(service);
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
			_updateStatsWorker = new Thread(worker);
			_updateStatsWorker.start();
		}
		else {
			log.info("Not starting StatsUpdate worker as it is already running");
		}
	}
	 */

	/**
	 * Process an input crawled file
	 */
	private void processInputFile(WebResource page, TermPreprocessor tp) {

		if (page instanceof WebResourceNoneImpl) {
			// this is not a page, its a null object, so ignore
			return;
		}

		log.info("Beginning processing of " + page.getUrl());
		if (!service.pageExists(page.getUrl())) {
			// ignore this page because the crawler didn't create a db record for it
			return;
		}

		long pageId = service.getPage(page.getUrl()).getId();

		log.info("Page id '"+pageId+"' found for:"+page.getUrl());
		boolean useStemming = false;
		boolean useStopwords = true;
		CrawledTextParser parser = new CrawledTextParser(useStemming, useStopwords, _stopwords, tp);
		parser.processInput(page.getContent());

		log.warn("Using new service.addDocumentTerms!");
		service.addDocumentTerms(pageId, parser.getTermFrequencies());

		// delete when done processing
		_indexCount.incrementAndGet();

	}

}
