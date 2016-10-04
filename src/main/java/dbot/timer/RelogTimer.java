package dbot.timer;

import dbot.Bot;
import dbot.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by niklas on 28.09.16.
 */
public class RelogTimer implements Runnable {//TODO: name: RelogTimer
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.RelogTimer");
	private static boolean isRunning = false;
	private static int dcCount = 0;

	public RelogTimer() {
		LOGGER.debug("trying to start new RelogTimer");
		if (!isRunning) {
			isRunning = true;
			run();
		}
	}

	@Override
	public void run() {
		LOGGER.info("started new RelogTimer");
		try {
			Thread.sleep(20 * 60000);//20 min, sollte höher sein als reconnectdauer
			dcCount = 0;
			Bot.updateBot();
		} catch(InterruptedException e) {
			LOGGER.error("Error", e);
		} finally {
			isRunning = false;
		}
	}

	public static void addDC() {
		dcCount++;
		if (dcCount > 30) {
			LOGGER.error("over 30 DCs in 20 min, killing Bot");
			System.exit(1);
		}
	}
}
