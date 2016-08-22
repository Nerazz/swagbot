package dbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
class Bot {
	
	public static void main(String[] args) throws Exception {
		new Statics();//init
		IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(10).login();
		botClient.getDispatcher().registerListener(new Events(botClient));
		System.out.println("~Main~fertig~");
	}
}
