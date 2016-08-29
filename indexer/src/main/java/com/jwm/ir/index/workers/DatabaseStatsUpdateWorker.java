package com.jwm.ir.index.workers;

import com.jwm.ir.index.service.Service;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class DatabaseStatsUpdateWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(DatabaseStatsUpdateWorker.class);
	private Service service;
	public DatabaseStatsUpdateWorker(Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
	
		log.info("Beginning to run database summarizer");
		service.updateSummaries();
		log.info("Finished running database summarizer");
	}

}
