package dbot;

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
		try {
			IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(10).login();
			Statics.BOT_CLIENT = botClient;
			botClient.getDispatcher().registerListener(new Events());
			System.out.println("~Main~fertig~");
		} catch(DiscordException e) {
			e.printStackTrace();//TODO: besser machen
		}
	}
}
