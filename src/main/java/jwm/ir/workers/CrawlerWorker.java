package jwm.ir.workers;

import jwm.ir.crawlerutils.UrlUtils;
import jwm.ir.crawlerutils.WebPage;
import jwm.ir.utils.Database;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class CrawlerWorker implements Runnable {

	private final int MAX_QUEUED_FILES_BEFORE_REST = 100;
	private final int MAX_URL_SUBMIT_BATCH_SIZE = 150;
	ArrayList<String> _frontier = new ArrayList<String>();

	final private static Logger log = LogManager.getLogger(CrawlerWorker.class);
	private int _id;
	private ArrayList<String> _validPageExtensions;
	private ArrayList<String> _validDomainExtensions;
	Database _db;
	File _documentDir;
	boolean _indexersRunning;
	AtomicBoolean _stopApp;
	PerformanceStatsUpdateWorker _perfWorker;
	
	/**
	 * @param args
	 */
	public CrawlerWorker(int crawlerNum, 
			ArrayList<String> validPageExtensions,
			ArrayList<String> validDomainExtensions,
			Database db,
			File documentDir, 
			boolean indexersRunning,
			PerformanceStatsUpdateWorker perfWorker,
			AtomicBoolean stopApp) {
		_id = crawlerNum;
		_db = db;
		_documentDir = documentDir;
		_indexersRunning = indexersRunning;
		log.info("Starting...");
		_validPageExtensions = validPageExtensions;
		_validDomainExtensions = validDomainExtensions;
		_perfWorker = perfWorker;
		_stopApp = stopApp;
	}

	@Override
	public void run() {
//    	testGetPagesToCrawl(dbService);
//    	testUrlCode();
		mainCrawl();
	}
	
	private void testUrlCode() {
		try {
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com", "http://jefftron.com/"));
			System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/TestPages/a/TestPage.html"));
			System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/TestPage.html", "./a/TestPageA1"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/TestPage.html", "./a/TestPageA1"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/TestPage.html", "./a/TestPageA1"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/TestPage.html", "a/TestPageA2"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/TestPage.html", "../TestPages/a/TestPageA3"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/TestPages/a/TestPage.html", "/WebApps/SearchEngine/TestPages/TestPageA4"));
			
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/Test/a/Page.html", "../../../../Page2.html"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/Test/Page.html", "./Page2.html"));
			System.out.println(UrlUtils.getAbsoluteUrlFromHyperlink("http://jefftron.com/Test/Page.html", "/Page2.html"));
    		System.out.println(UrlUtils.getLevelDepthOfRelativeLink(0, "../../Page.html"));
    		System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/Test/Page.html"));
    		System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/Test/foo"));
    		System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/Test/foo/"));
    		System.out.println(UrlUtils.getPathToUrlResource("http://jefftron.com/Test/"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void waitIfTooManyFilesAreQueued() {
		while(tooManyFilesAreQueued()) {
			log.info("*** WAITING *** More than " + MAX_QUEUED_FILES_BEFORE_REST + " files have been downloaded by this crawler, so resting...");
			sleep(30);
		}
	}
	
	private boolean tooManyFilesAreQueued() {
		
		if (!_indexersRunning) return false;
		
		File[] filesInDir = _documentDir.listFiles();
		int countFilesByThisCrawler = 0;
		boolean timeForARest = false;
		for(File f : filesInDir) {
			if (f.getName().startsWith(getOutputFilePrefix())) {
				countFilesByThisCrawler++;
				if (countFilesByThisCrawler > MAX_QUEUED_FILES_BEFORE_REST) {
					timeForARest = true;
					break;
				}
			}
		}
		
		return timeForARest;
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
                start = System.currentTimeMillis();
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
                        p.writeToFile(getOutputFilePrefix(), _documentDir.getAbsolutePath());
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

	private String getClientName() { return "Crawler#" + Integer.toString(_id); }

	private String getOutputFilePrefix() {
		return "crawler" + _id;
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
