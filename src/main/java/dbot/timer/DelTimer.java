package dbot.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class DelTimer implements Runnable {//TODO: vielleicht statt neuen Threads messages in list und über MainTimer löschen
	private static final Logger logger = LoggerFactory.getLogger("dbot.timer.DelTimer");
	private IMessage message = null;
	private int duration = 60000;
	
	public DelTimer(IMessage message, int duration) {
		this.message = message;
		this.duration = duration;
		Thread tDelTimer = new Thread(this, "DelTimer Thread");
		tDelTimer.start();
	}
	
	public void run() {
		try {
			if (message != null) {
				Thread.sleep(duration);
				message.delete();
			} else {
				logger.warn("tried to delete null-message");
			}
		} catch(MissingPermissionsException | RateLimitException | InterruptedException | DiscordException e) {
			logger.error("Error while deleting message", e);
		}
	}
}
