package dbot.comm;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.AudioChannel;

import java.io.*;

public class Play {
	
	/*static final String VICE = "Maiki Vanics - Vice(Original Mix).mp3";
	static final String HIGHNOON = "highnoon.mp3";
	static final String UNCHARTED = "Henry Saiz - Uncharted - ( Bedrock Records ).mp3";
	static final String HERB = "Age of Empires II Taunts11 Herb laugh.mp3";
	static final String WOLO = "Age of Empires II Taunts30 Wololo.mp3";
	static final String STGA = "Start the game already.mp3";
	static final String MANGO = "X-Coast - Mango Bay.mp3";*/
	
	public static void m(IVoiceChannel vChannel, String title) {
		//IVoiceChannel vChannel = guild.getVoiceChannelByID("189459280590667778");
		/*switch (title) {
			case "noon":
				title = HIGHNOON;
				break;
			case "vice":
				title = VICE;
				break;
			case "uncharted":
				title = UNCHARTED;
				break;
			case "herb":
				title = HERB;
				break;
			case "wolo":
				title = WOLO;
				break;
			case "14":
				title = STGA;
				break;
			case "mango":
				title = MANGO;
				break;
			
			default:
				System.out.println("mp3 nicht gefunden.");
				return;
		}*/
		
		
		try {
			vChannel.join();
			//join dings event
			AudioChannel aChannel = vChannel.getAudioChannel();
			//aChannel.join();
			File file = new File("sounds/" + title + ".mp3");
			//File testFile = new File();
			//System.out.println(testFile);
			aChannel.queueFile(file);
			
			//aChannel.queueUrl("http://quotes.adwww.de/play/Mami-mami-hilfe-hilfe__390");
			aChannel.setVolume(1.0f);
			//aChannel.resume();
		/*} catch(IOException ioe) {
			System.out.println("file not found: " + ioe);*/
		} catch(Exception e) {
			System.out.println("fail: " + e);
		}
	}
	
	/*@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		if 
	}*/
}
