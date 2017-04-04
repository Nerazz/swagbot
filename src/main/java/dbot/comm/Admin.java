package dbot.comm;

import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

/**
 * Admin commands
 *
 * @author Niklas Zd
 * @since 04.03.2017
 */
final class Admin {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Admin");

	/**
	 * forces the bot to logout and exits program
	 *
	 * @param message not used
	 */
	static void forceLogout(IMessage message) {
		try {
			Statics.BOT_CLIENT.logout();
			System.exit(0);
		} catch (DiscordException e) {
			LOGGER.error("Error while logging out", e);
		}
	}

}
