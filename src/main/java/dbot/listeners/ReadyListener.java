package dbot.listeners;

import dbot.timer.MainTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Niklas on 23.02.2017.
 */
public class ReadyListener implements IListener<ReadyEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.ReadyListener");

	@Override
	public void handle(ReadyEvent event) {
		LOGGER.info("BotReadyEvent");
		synchronized (GuildCreateListener.LOCK) {
			GuildCreateListener.LOCK.notifyAll();
		}
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		MainTimer mainTimer = new MainTimer();
		try {
			executor.scheduleAtFixedRate(mainTimer, 5, 10, TimeUnit.SECONDS);//TODO: care, 60sec
		} catch(Exception e) {
			e.printStackTrace();//TODO: log
		}
	}

}
