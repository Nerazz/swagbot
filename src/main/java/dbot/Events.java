package dbot;

import dbot.comm.Commands;
import dbot.comm.Flip;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Timer;

class Events {
	private static boolean bInit = false;
	private static IGuild guild;
	private static Database database;

	Events() {}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		System.out.println("~GuildCreateEvent~");
		if (!bInit) {
			IDiscordClient botClient = Statics.BOT_CLIENT;
			Statics.GUILD = botClient.getGuildByID(Statics.ID_GUILD);
			guild = Statics.GUILD;
			System.out.println("Bot joined guild: " + guild.getName());
			Flip.init();
			database = Database.getInstance();
			database.load();
			new Timer().schedule(new MainTimer(), 5000, 6000);
			bInit = true;
			System.out.println("Everything initialized");
		}
		System.out.println("~BOT~READY~");
	}

	/*@EventSubscriber
	public void onDiscordDisconnectedEvent() {
		System.out.println("DISCONNECTED!!");
		//DB.save();??
	}*/
	
	@EventSubscriber
	public synchronized void onMessageEvent(MessageReceivedEvent event) {//synchronized richtig hier?
		IMessage message = event.getMessage();
		if (database.containsUser(message.getAuthor())) {
			Commands.trigger(message);
		} else {
			System.out.println("Typing user not in Database!");
		}
	}
	
	@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {
		try {
			event.getUser().addRole(guild.getRolesByName("Newfags").get(0));
			System.out.println("added Role to " + event.getUser().getName());
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		} catch(DiscordException | RateLimitException e) {//TODO: Exceptions fixen
			e.printStackTrace();
		}
	}
	
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
