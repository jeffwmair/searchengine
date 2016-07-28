package jwm.ir.workers;

import jwm.ir.utils.Database;
import jwm.ir.utils.Db;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceStatsUpdateWorker implements Runnable {

	final private static Logger log = LogManager.getLogger(PerformanceStatsUpdateWorker.class);
	private Db _db;
	private AtomicInteger _pagesVerified = new AtomicInteger();
	private AtomicInteger _pagesCrawled = new AtomicInteger();
	private AtomicInteger _pagesIndexed = new AtomicInteger();
	private AtomicBoolean _stopFlag = new AtomicBoolean();

	public PerformanceStatsUpdateWorker(Db db, AtomicBoolean stopFlag) {
		_db = db;
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
			
			log.info("Beginning to run performance stats updater, PagesVerified: "+pagesVerified+", PagesCrawled: "+pagesCrawled+", PagesIndexed: " + pagesIndexed);
			_db.addPerformanceStats(pagesVerified, pagesCrawled, pagesIndexed);
		}
		
	}

}
