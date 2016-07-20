package dbot;

//import org.slf4j.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import java.util.List;
import java.util.*;
/**
 * Bot - Mainclass
 * @author	Niklas Zd
 */
public class Bot {
	

	 
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
	  * events in verschiedene dateien splitten?
	  * was ueberlegen fuer schonende sd-kartennutzung (fertig?)
	  * rpg / duelle um punkte / kaufbare pots und ähnliches
	  * topten fuer points (eher kleiner)
	  * topten fuer karma
	  * pollsystem
	  * alle events in extra classdateien
	  * json pimpen
	  * file fuer database
	  * file fuer sprueche, items, ...
	  * leute fuer stunde oder so umbenennen
	  * QUEUE FUER RATELIMIT (5 pro 5 sek)
	  * random exp
	  * top5
	  * stacktraces mit ordentlichem ersetzen
	  * xp pots
	  * while fuer levelup
	  * nice brackets und so
	  * ordenliches exit mit save(und ohne)
	  * fail fixen (events)
	  */
	  
	  /*
	   * Threads = thNAME
	   * Lists = lNAME
	   * Boolean = bNAME
	   * Temps = tNAME
	   * Flag = fNAME
	   * Data = dName
	   */
	
	public static IDiscordClient botClient;
	//protected static DataBase DB;/* = new DataBase();*/
	private static Statics statics;//um referenz nicht zu verlieren?
	
	public static void main(String[] args) throws Exception {
		statics = new Statics();
		
		botClient = new ClientBuilder().withToken(Statics.BOT_TOKEN).withReconnects().login();
		botClient.getDispatcher().registerListener(new Events(botClient));
		System.out.println("~Main~fertig~");
	}
}
