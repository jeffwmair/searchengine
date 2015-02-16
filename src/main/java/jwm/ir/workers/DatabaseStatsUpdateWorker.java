package jwm.ir.workers;

import jwm.ir.utils.Database;
import jwm.ir.utils.Log;

public class DatabaseStatsUpdateWorker implements Runnable {


	Database _db;
	Log _log;
	String CLIENT_NAME = "DatabaseSummarizerWorker";
	public DatabaseStatsUpdateWorker(Database db, Log log) {
		_db = db;
		_log = log;
	}
	
	@Override
	public void run() {
	
		_log.LogMessage(CLIENT_NAME, "Beginning to run database summarizer", false);
		_db.updateSummaries(CLIENT_NAME);		
		_log.LogMessage(CLIENT_NAME, "Finished running database summarizer", false);
	}

}
