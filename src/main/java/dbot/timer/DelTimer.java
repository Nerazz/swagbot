package dbot.timer;

import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.*;

import static dbot.util.Poster.post;

public class DelTimer implements Runnable {//TODO: vielleicht statt neuen Threads messages in list und über MainTimer löschen
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.DelTimer");
	//private static final MessageList MESSAGE_LIST = new MessageList(Statics.BOT_CLIENT, Statics.BOT_CLIENT.getChannelByID(Statics.ID_BOTSPAM));
	private IMessage message = null;
	private int duration = 60000;
	
	public DelTimer(IMessage message, int duration) {
		this.message = message;
		this.duration = duration;
		//MESSAGE_LIST.add(message);
		Thread tDelTimer = new Thread(this, "DelTimer Thread");
		tDelTimer.start();
	}

	/*public static void checkList() {
		post(MESSAGE_LIST.toString());
	}

	public static void deleteAll() {
		if (MESSAGE_LIST.size() > 2) {
			RequestBuffer.request(() -> {
				try {
					MESSAGE_LIST.bulkDelete(MESSAGE_LIST);//TODO: fixen?
					MESSAGE_LIST.clear();
				} catch (MissingPermissionsException | DiscordException e) {
					LOGGER.error("failed deleting all messages", e);
				}
			});
		}

	}

	public static void add(IMessage message) {
		MESSAGE_LIST.add(message);
	}


	public static void remove(IMessage message) {
		MESSAGE_LIST.remove(message);
	}*/

	@Override
	public void run() {
		try {
			if (message != null) {
				Thread.sleep(duration);
				//MESSAGE_LIST.remove(message);
				message.delete();
			} else {
				LOGGER.warn("tried to delete null-message");
			}
		} catch(MissingPermissionsException | RateLimitException | InterruptedException | DiscordException e) {
			LOGGER.error("Error while deleting message", e);
		}
	}
}
