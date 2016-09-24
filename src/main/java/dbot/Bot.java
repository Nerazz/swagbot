package dbot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
class Bot {
	
	public static void main(String[] args) {//TODO: auf maingilde rollen anpassen(bot sieht nur botspam)
		Statics.init();
		Logger logger = LoggerFactory.getLogger("dbot.Bot");
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
		try {
			IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(10).login();
			Statics.BOT_CLIENT = botClient;
			botClient.getDispatcher().registerListener(new Events());
			logger.debug("logged in");
		} catch(DiscordException e) {
			logger.error("Error while logging in", e);
		}
	}
}
