package dbot.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.*;

/**
 * handles deleting of messages
 *
 * @author Niklas Zd
 */
public final class DelTimer implements Runnable {//TODO: vielleicht statt neuen Threads messages in list und über TickTimer löschen
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.DelTimer");
	//private static final MessageList MESSAGE_LIST = new MessageList(Statics.BOT_CLIENT, Statics.BOT_CLIENT.getChannelByID(Statics.ID_BOTSPAM));
	/** message to be deleted */
	private IMessage message = null;
	/** standard duration until deletion */
	private int duration = 60000;

	/**
	 * sets the timer up to delete the message
	 *
	 * @param message message to be deleted
	 * @param duration duration until deletion
	 */
	public DelTimer(IMessage message, int duration) {//TODO: deltimer besser machen, vielleicht messages in 5 sekunden intervallen zusammen löschen oder so?/ alle 5 min batchdelete?
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

	/**
	 * waits duration and deletes the message
	 */
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
		} catch(MissingPermissionsException | RateLimitException | InterruptedException | DiscordException e) {//TODO: requesten, nicht ratelimit catchen
			LOGGER.error("Error while deleting message", e);
		}
	}
}
