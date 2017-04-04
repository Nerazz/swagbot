package dbot.comm;

import dbot.comm.items.Reminder;
import dbot.comm.items.Xpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.*;

/**
 * searches for item that gets bought
 * loads methods of items on load of class
 *
 * @author Niklas Zd
 */
final class Buy {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Buy");
	/** map of all items and connected methods */
	private static final Map<String, Consumer<IMessage>> ITEM_MAP = new HashMap<>();

	static {
		ITEM_MAP.put("xpot", Xpot::main);//TODO:
	}

	/**
	 * searches wanted item
	 *
	 * @param message for content
	 */
	static void main(IMessage message) {
		String content = message.getContent().toLowerCase();

		Matcher matcher = Pattern.compile("^!buy\\s([a-z]+)").matcher(content);
		if (!matcher.lookingAt()) {
			LOGGER.error("Matcher matcht nicht!({})", content);
			return;
		}

		switch (matcher.group(1)) {//TODO: itemhashmap
			case "xpot":
				Xpot.main(message);
				break;

			case "reminder":
				Reminder.main(message);
				break;

			default:
				break;
			}
		}
}
