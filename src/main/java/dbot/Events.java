package dbot;

import dbot.comm.Commands;
import dbot.comm.Flip;

import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;

import java.util.Timer;

class Events {
	private static IGuild guild;
	//protected static int nBrag = 0;
	private static DataBase DB;
	private static ServerData SD;
	private static boolean bInit = false;
	
	//private String[] sBrags = new String[] {"RUHE HIER!!elf", "Git off mah lawn", "Ihr kleinen Kinners kriegt gleich ordentlich aufs Maul", "Wer reden kann, muss auch mal die Schnauze halten können!", "HALT STOPP, JETZT REDE ICH", "S T F U B O Y S", "Wengier labern, sonst gibts Vokabeltest!", "Psst ihr Ottos"};
	
	Events() {}

	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("~BotReadyEvent~");
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		System.out.println("~GuildCreateEvent~");
		if (!bInit) {
			IDiscordClient botClient = Statics.BOT_CLIENT;
			Statics.GUILD = botClient.getGuildByID(Statics.ID_GUILD);
			guild = Statics.GUILD;
			System.out.println("Bot joined guild: " + guild.getName());
			DataBase.init();
			DB = new DataBase();
			DB.load();
			SD = DB.getServerData();
			Flip.init(SD.getFlipRoomID());
			Flip flip = new Flip();
			Commands.init(DB, flip);
			Timer timer = new Timer();
			MainTimer mainTimer = new MainTimer();
			timer.schedule(mainTimer, 5000, 6000);
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
		if (DB.containsUser(message.getAuthor())) {
			Commands.trigger(message);
		} else {
			System.out.println("Typing user not in Database!");
		}
	}

	static DataBase getDB() {
		return DB;
	}

	static ServerData getSD() {
		return SD;
	}
	
	/*@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {//TODO: addrole (vielleicht bei jedem timertick gucken, wer keine rolle hat und die +newrole? (wegen cached role))
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
