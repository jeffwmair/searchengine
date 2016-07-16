package jwm.ir.workers;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import jwm.ir.utils.Database;
import jwm.ir.utils.Log;

public class PerformanceStatsUpdateWorker implements Runnable {

	private Database _db;
	private Log _log;
	private String CLIENT_NAME = "PerformanceStatsWorker";
	private AtomicInteger _pagesVerified = new AtomicInteger();
	private AtomicInteger _pagesCrawled = new AtomicInteger();
	private AtomicInteger _pagesIndexed = new AtomicInteger();
	private AtomicBoolean _stopFlag = new AtomicBoolean();
	private int _workers;

	public PerformanceStatsUpdateWorker(Database db, Log log, int workers, AtomicBoolean stopFlag) {
		_db = db;
		_log = log;
		_workers = workers;
		_stopFlag = stopFlag;
	}

	public void incrementPagesVerified() { _pagesVerified.addAndGet(1); }
	public void incrementPagesCrawled() { _pagesCrawled.addAndGet(1); }
	public void incrementPagesIndexed() { _pagesIndexed.addAndGet(1); }
	
	@Override
	public void run() {

		while(true && !_stopFlag.get())
		{
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int pagesVerified = _pagesVerified.getAndSet(0);
			int pagesCrawled = _pagesCrawled.getAndSet(0);
			int pagesIndexed = _pagesIndexed.getAndSet(0);
			
			_log.LogMessage(CLIENT_NAME, "Beginning to run performance stats updater, PagesVerified: "+pagesVerified+", PagesCrawled: "+pagesCrawled+", PagesIndexed: " + pagesIndexed, false);
			_db.addPerformanceStats(CLIENT_NAME, _workers, pagesVerified, pagesCrawled, pagesIndexed);		
		}
		
	}

}
