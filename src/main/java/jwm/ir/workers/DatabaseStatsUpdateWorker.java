package jwm.ir.workers;

import jwm.ir.utils.Database;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class DatabaseStatsUpdateWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(DatabaseStatsUpdateWorker.class);
	private Database _db;
	public DatabaseStatsUpdateWorker(Database db) {
		_db = db;
	}
	
	@Override
	public void run() {
	
		log.info("Beginning to run database summarizer");
		_db.updateSummaries();
		log.info("Finished running database summarizer");
	}

}
