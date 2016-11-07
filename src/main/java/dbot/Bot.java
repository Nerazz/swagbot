package dbot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import dbot.comm.Posts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;

/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
public class Bot {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.Bot");//TODO: alle final logger capslock
	private static final Events EVENTS = new Events();
	private static IDiscordClient botClient = null;

	public static void main(String[] args) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
		updateBot();
		/*String data[] = {"gems", "name", "level"};
		SQLData swag = SQLPool.getData("97092184821465088", data);
		System.out.println(swag.get("name"));*/
		//System.out.println(SQLPool.getScoreList());
		//Posts.top();
	}

	public static void updateBot() {
		if (botClient != null) {
			if (botClient.isReady()) return;
			botClient.getDispatcher().unregisterListener(EVENTS);
		}
		try {
			botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(5).login();//0.25 * pow(2,x), 0 < x < 5[min] -> 5 sind ca 16min gesamt
			Statics.BOT_CLIENT = botClient;
			botClient.getDispatcher().registerListener(EVENTS);
			LOGGER.debug("created new Bot");
		} catch(DiscordException e) {
			LOGGER.error("Error while creating new Bot", e);
		}
	}
}
