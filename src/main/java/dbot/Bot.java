package dbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
class Bot {

	 /*TODO:
	  * logger
	  * eigenes package fuer timer
	  * votesystem!!
	  *	karmasystem für bilder
	  * roulette fixen
	  * losen zum momentanen roulette machen
	  * losen 10% oder so abgezgen vom lospool
	  * bei glueckspsielen bei jedem update pool anzeigen + infos wie gewinnchance?
	  * botavatar nach status aendern
	  * was ueberlegen fuer schonende sd-kartennutzung (fertig?)
	  * rpg / duelle um punkte / kaufbare pots und ähnliches
	  * topten fuer points (eher kleiner)
	  * topten fuer karma
	  * pollsystem
	  * file fuer database
	  * file fuer sprueche, items, ...
	  * leute fuer stunde oder so umbenennen
	  * top5
	  * stacktraces mit ordentlichem ersetzen
	  * xp pots
	  * nice brackets und so
	  * ordenliches exit mit save(und ohne)
	  * fail fixen (events)
	  */
	  
	  /*
	   * Threads = thNAME
	   * Lists = lNAME
	   * Boolean = bNAME
	   * Temps = tNAME; tmpNAME?
	   * Flag = fNAME
	   * Data = dName
	   */
	
	public static void main(String[] args) throws Exception {
		new Statics();//init
		IDiscordClient botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).setMaxReconnectAttempts(10).login();
		botClient.getDispatcher().registerListener(new Events(botClient));
		System.out.println("~Main~fertig~");
	}
}
