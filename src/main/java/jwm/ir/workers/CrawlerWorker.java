package jwm.ir.workers;

import jwm.ir.crawler.WebPage;
import jwm.ir.indexer.ParsedWebPage;
import jwm.ir.utils.Database;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class CrawlerWorker implements Runnable {

	private final int MAX_QUEUED_FILES_BEFORE_REST = 100;
	private final int MAX_URL_SUBMIT_BATCH_SIZE = 150;
	ArrayList<String> _frontier = new ArrayList<>();

	final private static Logger log = LogManager.getLogger(CrawlerWorker.class);
	private int _id;
	private ArrayList<String> _validPageExtensions;
	private ArrayList<String> _validDomainExtensions;
	Database _db;
	boolean _indexersRunning;
	AtomicBoolean _stopApp;
	PerformanceStatsUpdateWorker _perfWorker;
	private final BlockingQueue<ParsedWebPage> indexQueue;
	
	public CrawlerWorker(int crawlerNum,
						 ArrayList<String> validPageExtensions,
						 ArrayList<String> validDomainExtensions,
						 Database db,
						 BlockingQueue<ParsedWebPage> indexQueue,
						 boolean indexersRunning,
						 PerformanceStatsUpdateWorker perfWorker,
						 AtomicBoolean stopApp) {
		if (indexQueue == null) throw new IllegalArgumentException("Must provide indexQueue");
		this.indexQueue = indexQueue;
		_id = crawlerNum;
		_db = db;
		_indexersRunning = indexersRunning;
		_validPageExtensions = validPageExtensions;
		_validDomainExtensions = validDomainExtensions;
		_perfWorker = perfWorker;
		_stopApp = stopApp;

		log.info("Starting...");
	}

	@Override
	public void run() {
		mainCrawl();
	}
	
	private void waitIfTooManyFilesAreQueued() {
		while(tooManyFilesAreQueued()) {
			log.info("*** WAITING *** More than " + MAX_QUEUED_FILES_BEFORE_REST + " files have been downloaded by this crawler, so resting...");
			sleep(30);
		}
	}
	
	private boolean tooManyFilesAreQueued() {
		
		if (!_indexersRunning) return false;
		return indexQueue.size() > MAX_QUEUED_FILES_BEFORE_REST;

	}
	
	private void mainCrawl() {
	
        while (true && !_stopApp.get()) {

            waitIfTooManyFilesAreQueued();

            // bring some urls out of the database for crawling
            long start = System.currentTimeMillis();
            log.info("Starting populate frontier");
            populateUrlFrontier(_frontier);
            log.info("Populated URL frontier from database with " + _frontier.size() + " links: " + (System.currentTimeMillis() - start) + "ms");

            ArrayList<String> tempUrlList = new ArrayList<String>();
            for (String url : _frontier) tempUrlList.add(url);

            HashMap<String,String> urlsWithAnchorTexts = new HashMap<String, String>();

            for (String url : tempUrlList) {

                if (_stopApp.get()) break;

                WebPage p = new WebPage(url);
                String title = null;
                String pageDesc = null;
                boolean success = p.crawl();
                if (success) {
                    // lets say we just count successful crawls with the performance worker
                    _perfWorker.incrementPagesCrawled();
                    title = p.getPageTitle();
                    pageDesc = p.getPageDescription();
                    boolean[] robotRules = new boolean[2];
                    p.getRobotMetaRules(robotRules);
                    boolean noIndex = robotRules[0];
                    boolean noFollow = robotRules[1];

                    if (!noIndex) {
						log.info("Adding page to queue:"+p.getPageTitle());
						indexQueue.add(p.getParsedPage());
                    }

                    if (!noFollow) {
                        start = System.currentTimeMillis();
                        log.info("Starting add hyperlinks");
                        urlsWithAnchorTexts = p.getHyperlinks(_validPageExtensions, _validDomainExtensions);
                        /* TODO: add pagelinks in this addUrls call!! */
                        addUrls(url, urlsWithAnchorTexts);
                        log.info("Added "+ urlsWithAnchorTexts.size() +" hyperlinks to database: " + (System.currentTimeMillis() - start) + "ms");
                    }
                }

                start = System.currentTimeMillis();
                log.info("Starting add page crawl result");
                _db.addCrawlResult(url, title, pageDesc, new Date(), success);
                log.info("Added page crawl result to database (with "+urlsWithAnchorTexts.size()+" outlinks): " + (System.currentTimeMillis() - start) + "ms");
                _frontier.remove(url);
            }

        }
	}
	
	private void populateUrlFrontier(ArrayList<String> frontier) {

		if (_stopApp.get()) return;
		
		frontier.clear();
		
		HashMap<String,String> urlsDomains = _db.getNextPagesForCrawling(_id);
		if (urlsDomains.size() > 0) {
			log.info("Retrieved list of domains of size:" + urlsDomains.size());
		}
		for(Map.Entry<String, String> items : urlsDomains.entrySet()) {
			frontier.add(items.getKey());
		}
		
		if (frontier.size() == 0) {
			sleep(2);
//			if (_id > 1) {
//				_db.overtakeUncrawledDomain(getClientName(), _id);	
//			}
			populateUrlFrontier(frontier);			
		}
	}
			
	private void addUrls(String foundInPage, HashMap<String,String> urlsWithAnchorText) {

		if (urlsWithAnchorText == null || urlsWithAnchorText.size() == 0) {
			return;
		}
		
		/* TODO: do something with anchor text later? */
		
		
		/* this submits batches of urls to the database */
		try
		{
			ArrayList<String> urls = new ArrayList<String>();
			int batchSize = 0;
			for(Map.Entry<String, String> entry : urlsWithAnchorText.entrySet()) {
			
				urls.add(entry.getKey());
				batchSize++;
				
				if (batchSize == MAX_URL_SUBMIT_BATCH_SIZE) {
					_db.addNewUrls(_id, foundInPage, urls);
					urls.clear();
					batchSize = 0;
				}
			}
			if (urls.size() > 0) _db.addNewUrls(_id, foundInPage, urls); 	// add the last batch
		}
		catch(Exception ex)
		{
			log.error("Error in addUrls():" + ex.toString());
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
