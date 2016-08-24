package com.jwm.ir.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jeff on 2016-07-28.
 */
public class TerminationWatcher implements Runnable {
	final private static Logger log = LogManager.getLogger(TerminationWatcher.class);
	private AtomicBoolean stopApplication;

	public TerminationWatcher(AtomicBoolean stopApplication) {
		this.stopApplication = stopApplication;
	}

	@Override
	public void run() {

		/* this thread can check for a stop flag */
		File flagsDir = new File("./flags");
		if (!flagsDir.exists()) {
			flagsDir.mkdir();
		}

		while (true) {

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			File stopFlag = new File("./flags/stop.txt");
			if (stopFlag.exists()) {
				stopFlag.delete();
				log.info("Found flags/stop.txt,  so stopping the application");
				stopApplication.set(true);
				break;
			}

		}
	}
}
