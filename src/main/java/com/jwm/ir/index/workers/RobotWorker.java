package com.jwm.ir.index.workers;

import com.jwm.ir.entity.Page;
import com.jwm.ir.index.crawler.RobotsTxt;
import com.jwm.ir.index.crawler.UrlUtils;
import com.jwm.ir.index.service.Service;
import com.jwm.ir.utils.HttpUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class RobotWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(RobotWorker.class);

	private final int MAX_ROBOT_CACHE_SIZE = 250;
	private final Service service;
	private AtomicBoolean _stopApp;
	public RobotWorker(AtomicBoolean stopApp, Service service) {
		this.service = service;
		_stopApp = stopApp;
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
		
		
		HashMap<String, RobotsTxt> domainRobots = new HashMap<>();
		
		while (true && !_stopApp.get()) {
			
			if (domainRobots.size() > MAX_ROBOT_CACHE_SIZE) {
				// trim it down
				HashMap<String, RobotsTxt> temp = new HashMap<>();
				int i = 0;
				log.info("DomainRobot size is > MAX_ROBOT_CACHE_SIZE, so trimming now...");
				for(Map.Entry<String, RobotsTxt> item : domainRobots.entrySet()) {
					temp.put(item.getKey(), item.getValue());
					i++;
					if (i > MAX_ROBOT_CACHE_SIZE - 50) break;
				}
				
				domainRobots.clear();
				for(Map.Entry<String, RobotsTxt> item : temp.entrySet()) {
					domainRobots.put(item.getKey(), item.getValue());
				}
				log.info("DomainRobot size is now " + domainRobots.size());
			}
			
			long start = System.currentTimeMillis();
			log.info("Starting to get urls to verify");
			List<Page> unverifiedPages = service.getPages(Service.FilterVerified.UnverifiedOnly);
			log.info("Got urls to verify:" + (System.currentTimeMillis() - start) + "ms");
			if (unverifiedPages.size() == 0) {
				log.info("No unverified pages found");
				sleep(1);
				continue;
			}
			
				List<String> verificationResults = new ArrayList<>();
			
			try 
			{
				for(Page p : unverifiedPages) {
					RobotsTxt rbt = getRobot(p.getUrl(), domainRobots);
					if (rbt.canCrawl(p.getUrl())) {
						verificationResults.add(p.getUrl());
					}
				}
				
				start = System.currentTimeMillis();
				log.info("Starting to update verification status");
				for(String s : verificationResults) {
					log.debug("Verified -> "+s);
				}
				service.setUrlsAsVerified(verificationResults);
				log.info("Updated verification status in database for " + unverifiedPages.size() + " pages:" + (System.currentTimeMillis() - start) + "ms");

				PageRankCalculatorWorker pageRankCalculatorWorker = new PageRankCalculatorWorker(service);
				pageRankCalculatorWorker.run();
				DatabaseStatsUpdateWorker statsUpdater = new DatabaseStatsUpdateWorker(service);
				statsUpdater.run();

			} 
			catch (Exception ex) {
				log.error("Error in robotWorker:" + ex.toString() + "\n Line:" + Thread.currentThread().getStackTrace()[0].getLineNumber());
			}
						
			// sleep to allow for more pages to be collected
			sleep(2);
		}
		
	}
	
	private RobotsTxt getRobot(String url, HashMap<String, RobotsTxt> domainRobots)  {
		
		/* get from cache or from the source */
		
		RobotsTxt rbt;
		String domain = null;
		
		try {
			domain = UrlUtils.getDomainFromAbsoluteUrl(url);
		} catch (Exception e) {
			
		}
		
		if (domain == null) {
			rbt = new RobotsTxt(false);
			log.error("Error getting jwm.ir.domain from url, so will be excluded " + url);
		}
		else {
			rbt = domainRobots.get(domain);
			if (rbt == null) {
				rbt = new RobotsTxt(domain);
				ArrayList<String> robotText = HttpUtils.getRobotTxtFromDomain(domain);
				for(String s : robotText) {
					rbt.processRobotTxtLine(s);
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

}
