package jwm.ir.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jwm.ir.crawlerutils.RobotsTxt;
import jwm.ir.crawlerutils.UrlUtils;
import jwm.ir.utils.*;


public class RobotWorker implements Runnable {


	private final int MAX_ROBOT_CACHE_SIZE = 250;
	private Database _db;
	private Log _log;
	private int _id;
	private AtomicBoolean _stopApp;
	private PerformanceStatsUpdateWorker _perfWorker;
	public RobotWorker(int robotNum, AtomicBoolean stopApp, PerformanceStatsUpdateWorker perfWorker, Log log, Database db) {
		_id = robotNum;
		_log = log;
		_db = db;
		_stopApp = stopApp;
		_perfWorker = perfWorker;
	}
	
	@Override
	public void run() {
		
//		RobotsTxt r = HttpUtils.getRobotTxtFromDomain("http://localhost/WebApps/SearchEngine/TestPages", getClientName(), _log);
//		ArrayList<String> out = HttpUtils.getRobotTxtFromDomain("http://microsoft.com", getClientName(), _log);
//		RobotsTxt r = new RobotsTxt("microsoft.com");
//		for(String s : out){
//			r.processRobotTxtLine(s);
//		}
//		return;
		
		
		HashMap<String, RobotsTxt> domainRobots = new HashMap<String, RobotsTxt>();
		
		while (true && !_stopApp.get()) {
			
			if (domainRobots.size() > MAX_ROBOT_CACHE_SIZE) {
				// trim it down
				HashMap<String, RobotsTxt> temp = new HashMap<String, RobotsTxt>();
				int i = 0;
				log("DomainRobot size is > MAX_ROBOT_CACHE_SIZE, so trimming now...", false);
				for(Map.Entry<String, RobotsTxt> item : domainRobots.entrySet()) {
					temp.put(item.getKey(), item.getValue());
					i++;
					if (i > MAX_ROBOT_CACHE_SIZE - 50) break;
				}
				
				domainRobots.clear();
				for(Map.Entry<String, RobotsTxt> item : temp.entrySet()) {
					domainRobots.put(item.getKey(), item.getValue());
				}
				log("DomainRobot size is now " + domainRobots.size(), false);
			}
			
			long start = System.currentTimeMillis();
			log("Starting to get urls to verify", false);
			ArrayList<String> pages = _db.getUnverifiedPagesForVerification(getClientName(), _id);
			log("Got urls to verify:" + (System.currentTimeMillis() - start) + "ms", false);
			if (pages.size() == 0) {
				log("No unverified pages found", false);
				sleep(10);
				continue;
			}
			
				HashMap<String, Integer> verificationResults = new HashMap<String, Integer>();
			
			try 
			{
				for(String url : pages) {
					RobotsTxt rbt = getRobot(url, domainRobots);
					start = System.currentTimeMillis();
					if (rbt.canCrawl(url, getClientName(), _log)) {
						_perfWorker.incrementPagesVerified();
						verificationResults.put(url, 1);
					}
				}
				
				start = System.currentTimeMillis();
				log("Starting to update verification status", false);
				_db.setVerificationStatusForUrls(getClientName(), verificationResults);
				log("Updated verification status in database for " + pages.size() + " pages:" + (System.currentTimeMillis() - start) + "ms", false);
				
			} 
			catch (Exception ex) {
				log("Error in robotWorker:" + ex.toString() + "\n Line:" + Thread.currentThread().getStackTrace()[0].getLineNumber(), true);
			}
						
			// sleep to allow for more pages to be collected
			sleep(2);
		}
		
	}
	
	private RobotsTxt getRobot(String url, HashMap<String, RobotsTxt> domainRobots)  {
		
		/* get from cache or from the source */
		
		RobotsTxt rbt = null;
		String domain = null;
		
		try {
			domain = UrlUtils.getDomainFromAbsoluteUrl(url);
		} catch (Exception e) {
			
		}
		
		if (domain == null) {
			rbt = new RobotsTxt(false);
			log("Error getting domain from url, so will be excluded " + url, true);
		}
		else {
			rbt = domainRobots.get(domain);
			if (rbt == null) {
				rbt = new RobotsTxt(domain);
				ArrayList<String> robotText = HttpUtils.getRobotTxtFromDomain(domain, getClientName(), _log);
				for(String s : robotText) {
					rbt.processRobotTxtLine(s, _log, getClientName());
				}
				domainRobots.put(domain, rbt);
			}
		}
		
		return rbt;
		
	}
	
	private void sleep(int seconds) {
		try 
		{
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
							
		}
	}
	private String getClientName() { return "Robot#" + Integer.toString(_id); }
	private void log(String msg, boolean err) {
		_log.LogMessage(getClientName(), msg, err);
	}

}
