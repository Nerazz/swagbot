package dbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
class Bot {
	
	public static void main(String[] args) throws Exception {//TODO: auf maingilde rollen anpassen(bot sieht nur botspam)
		Statics.init();
		IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(10).login();
		Statics.BOT_CLIENT = botClient;
		botClient.getDispatcher().registerListener(new Events());
		System.out.println("~Main~fertig~");//test
	}
}
