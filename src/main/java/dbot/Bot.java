package dbot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import dbot.listeners.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
final class Bot {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.Bot");

	public static void main(String[] args) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		StatusPrinter.print(lc);
		try {
			IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(5).login();//0.25 * pow(2,x), 0 < x < 5[min] -> 5 sind ca 16min gesamt
			Statics.BOT_CLIENT = botClient;
			EventDispatcher dispatcher = botClient.getDispatcher();
			dispatcher.registerListener(new ReadyListener());
			dispatcher.registerListener(new GuildCreateListener());
			dispatcher.registerListener(new DisconnectedListener());
			dispatcher.registerListener(new UserJoinListener());
			dispatcher.registerListener(new MessageListener());
			LOGGER.debug("Added Listeners");
		} catch(DiscordException e) {
			LOGGER.error("Error while creating new Bot", e);
		}

		//Object object = new Object();
		//URL url = object.getClass().getClassLoader().getResource("hikari.properties");
		//System.out.println(url.toString());
		/*try(Connection con = SQLPool.getDataSource().getConnection()) {
			System.out.println(con.toString());
		} catch(SQLException e) {
			e.printStackTrace();
		}*/
	}

	/*public static void updateBot() {
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
	}*/
}
