package dbot.comm;

import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Niklas on 04.03.2017.
 */
final class Admin {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Admin");

	static void forceLogout(IMessage message) {
		try {
			Statics.BOT_CLIENT.logout();
			System.exit(0);
		} catch (DiscordException e) {
			LOGGER.error("Error while logging out", e);
		}
	}

}
