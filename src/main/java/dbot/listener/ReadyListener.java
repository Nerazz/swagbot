package dbot.listener;

import dbot.comm.Lotto;
import dbot.timer.TickTimer;
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
public final class ReadyListener implements IListener<ReadyEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listener.ReadyListener");

	@Override
	public void handle(ReadyEvent event) {
		LOGGER.info("BotReadyEvent");
		synchronized (GuildCreateListener.LOCK) {
			GuildCreateListener.LOCK.notifyAll();
		}
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		TickTimer tickTimer = new TickTimer();
		try {
			executor.scheduleAtFixedRate(tickTimer, 5, 60, TimeUnit.SECONDS);//TODO: care, 60sec
		} catch(Exception e) {
			LOGGER.error("TickTimer RIP!!", e);
		}
		Lotto.init();
	}
}
