package com.jwm.ir.index.workers;

import com.jwm.ir.entity.Page;
import com.jwm.ir.index.crawler.WebPage;
import com.jwm.ir.index.message.WebResource;
import com.jwm.ir.index.service.Service;
import com.jwm.ir.utils.AssertUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class CrawlerWorker implements Runnable {

	private final int MAX_URL_SUBMIT_BATCH_SIZE = 150;
	ArrayList<String> _frontier = new ArrayList<>();

	final private static Logger log = LogManager.getLogger(CrawlerWorker.class);
	private final Service service;
	private final List<String> _validDomainExtensions;
	AtomicBoolean _stopApp;
	private final BlockingQueue<WebResource> indexQueue;

	public CrawlerWorker( List<String> validDomainExtensions,
						  Service service,
						  BlockingQueue<WebResource> indexQueue,
						  AtomicBoolean stopApp) {
		AssertUtils.notNull(indexQueue, "must provide indexQueue");
		AssertUtils.notNull(service, "must provide service");
		this.indexQueue = indexQueue;
		this.service = service;
		_validDomainExtensions = validDomainExtensions;
		_stopApp = stopApp;

		log.info("Starting...");
	}

	@Override
	public void run() {
		log.info("Started crawler.");
		mainCrawl();
	}

	private void mainCrawl() {

		while (true && !_stopApp.get()) {

			// bring some urls out of the database for crawling
			long start = System.currentTimeMillis();
			log.info("Starting populate frontier");
			populateUrlFrontier(_frontier);
			log.info("Populated URL frontier from database with " + _frontier.size() + " links: " + (System.currentTimeMillis() - start) + "ms");
			for(String s : _frontier) {
				log.debug("Frontier link:"+s);
			}

			ArrayList<String> tempUrlList = new ArrayList<>();
			for (String url : _frontier) tempUrlList.add(url);

			HashMap<String,String> urlsWithAnchorTexts = new HashMap<>();

			for (String url : tempUrlList) {

				if (_stopApp.get()) break;

				WebPage p = new WebPage(url);
				String title = null;
				String pageDesc = null;
				Page.CrawlResult result = p.crawl();
				if (result == Page.CrawlResult.Success) {
					// lets say we just count successful crawls with the performance worker
					title = p.getPageTitle();
					pageDesc = p.getPageDescription();
					boolean[] robotRules = new boolean[2];
					p.getRobotMetaRules(robotRules);
					boolean noIndex = robotRules[0];
					boolean noFollow = robotRules[1];

					if (!noIndex) {
						log.info("Adding page to queue:"+p.getPageTitle());
						indexQueue.add(p.getWebResource());
						log.info("ADDED page to queue:"+p.getPageTitle());
					}

					if (!noFollow) {
						start = System.currentTimeMillis();
						log.info("Starting add hyperlinks");
						urlsWithAnchorTexts = p.getHyperlinks(_validDomainExtensions);
                        /* TODO: add pagelinks in this addUrls call!! */
						addUrls(url, urlsWithAnchorTexts);
						log.info("Added "+ urlsWithAnchorTexts.size() +" hyperlinks to database: " + (System.currentTimeMillis() - start) + "ms");
					}
				}

				start = System.currentTimeMillis();
				log.info("Starting add page crawl result");
				service.addCrawlResult(url, title, pageDesc, result);
				log.info("Added page crawl result to database (with "+urlsWithAnchorTexts.size()+" outlinks): " + (System.currentTimeMillis() - start) + "ms");
				_frontier.remove(url);
			}

		}
	}

	private void populateUrlFrontier(ArrayList<String> frontier) {

		if (_stopApp.get()) return;

		frontier.clear();

		List<String> urlsDomains = service.getUrlsToCrawl();
		if (urlsDomains.size() > 0) {
			log.info("Retrieved list of domains of size:" + urlsDomains.size());
		}
		for(String item : urlsDomains) {
			frontier.add(item);
		}

		if (frontier.size() == 0) {
			sleep(2);
			populateUrlFrontier(frontier);
		}
	}

	private void addUrls(String parentUrl, HashMap<String,String> urlsWithAnchorText) {

		if (urlsWithAnchorText == null || urlsWithAnchorText.size() == 0) {
			return;
		}
		
		/* TODO: do something with anchor text later? */
		
		
		/* this submits batches of urls to the database */
		try
		{
			ArrayList<String> urls = new ArrayList<>();
			int batchSize = 0;
			for(Map.Entry<String, String> entry : urlsWithAnchorText.entrySet()) {

				urls.add(entry.getKey());
				batchSize++;

				if (batchSize == MAX_URL_SUBMIT_BATCH_SIZE) {
					for (String url : urls) {
						service.addUrlForCrawling(url, parentUrl);
					}
					urls.clear();
					batchSize = 0;
				}
			}
			if (urls.size() > 0) {
				// send the last batch
				for (String url : urls) {
					service.addUrlForCrawling(url, parentUrl);
				}
			}
		}
		catch(Exception ex)
		{
			log.error("Error in addUrls():" + ex.toString(), ex);
		}
	}

	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
