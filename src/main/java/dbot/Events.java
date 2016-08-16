package dbot;

import dbot.comm.*;

import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import java.io.*;

class Events {
	private int rnd;
	protected static int nBrag = 0;
	//private Poster pos;//static?
	//private Flip flip;
	
	private static IVoiceChannel vChannel = null;
	private static File file;
	
	
	static DataBase DB;
	static ServerData SD;
	static IDiscordClient botClient;
	static IGuild guild;
	private static boolean bInit = false;
	
	private String[] sBrags = new String[] {"RUHE HIER!!elf", "Git off mah lawn", "Ihr kleinen Kinners kriegt gleich ordentlich aufs Maul", "Wer reden kann, muss auch mal die Schnauze halten können!", "HALT STOPP, JETZT REDE ICH", "S T F U B O Y S", "Wengier labern, sonst gibts Vokabeltest!", "Psst ihr Ottos"};
	
	Events() {
		
	}
	
	Events(IDiscordClient botClient) {
		Events.botClient = botClient;
	}
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("~BotReadyEvent~");
		
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		System.out.println("~GuildCreateEvent~");
		if (!bInit) {
			guild = botClient.getGuildByID(Statics.ID_GUILD);
			System.out.println("Bot joined guild: " + guild.getName());
			Poster pos = new Poster(botClient, guild.getChannelByID(Statics.ID_BOTSPAM));//vorher schon alle channel initialisieren?
			DataBase.init(guild);
			DB = new DataBase();
			DB.load();
			SD = DB.getServerData();
			Flip.init(pos, SD.getFlipRoomID());
			Flip flip = new Flip();
			Commands.init(pos, DB, flip);
			new MainTimer();
			bInit = true;
			System.out.println("Everything initialized");
		}
		System.out.println("~BOT~READY~");
	}
	
	
	
	@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {
		System.out.println("DISCONNECTED!!");
		//DB.save();??
	}
	
	@EventSubscriber
	public synchronized void onMessageEvent(MessageReceivedEvent event) {//synchronized richtig hier?
		
		IMessage message = event.getMessage();
		if (message.getChannel() == guild.getChannelByID(Statics.ID_BOTSPAM)) {//1
			if (DB.containsUser(message.getAuthor())) {
				Commands.trigger(message);
			} else {
				System.out.println("Schreibenden User nicht gefunden.");
			}
		}
		
		//System.out.println(bClient.getGuilds().get(1).getRegion());
		/*try {
			IRegion tRegion = bClient.getRegions().get(1);
			System.out.println(tRegion);
			System.out.println(bClient.getGuilds().get(1));
			bClient.getGuilds().get(1).changeRegion(tRegion);
		} catch(DiscordException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		}*/
		
		
		/*else if (((channel == guild.getChannelByID(idLink)) && (ml.isIn(content, "http://") || ml.isIn(content, "https://"))) && false) {
			System.out.println("linkboys");
			int ID = 23;
			int x = 100;
			int y = 17;
			pos.post("Nicer Post, " + author + " mal sehen, wie gut der ankommt:\n\t\t\tID: "+ ID + "\t" + x + " :+1:\t" + y + ":-1:");
			try {
				Thread.sleep(3000);
				//tmpM.edit("test");
			} catch(Exception e) {
				
			}
		}*/
	}
	
	/*@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {
		event.getUser().addRole("97105817550999552", );
	}*/
	
/*	@EventSubscriber
	public void onUserVoiceChannelJoinEvent(UserVoiceChannelJoinEvent e) {
		if (e.getUser().getID().equals(idBot)) {
			System.out.println("jo");
			if (file != null) {
				try {
					Thread.sleep(2000);
					aChannel = e.getChannel().getAudioChannel();
					aChannel.queueFile(file);
				} catch(Exception ex) {
					System.out.println("voicechannel + " + ex);
				}
			}
			else {
				System.out.println("derber fail");
			}
		}
		else {
			System.out.println("nenene");
		}
	}*/

	
	
	/*private void playFile(AudioChannel vChannel, String path) throws Exception {
		//channel = guild.getVoiceChannelByID("97105817584553984");
		vChannel.join();
		File testFile = new java.io.File("");
		channel.queueFile(testFile);
		
	}*/
	
	
	
}
