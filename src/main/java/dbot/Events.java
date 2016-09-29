package dbot;

import dbot.comm.Commands;
import dbot.comm.Flip;
import dbot.timer.RelogTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Timer;

public class Events {
	private final static Logger LOGGER = LoggerFactory.getLogger("dbot.Events");
	private static boolean bInit = false;
	private static IGuild guild;
	private static final Database DATABASE = Database.getInstance();

	Events() {}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		if (!bInit) {
			DATABASE.load();
			Statics.GUILD = Statics.BOT_CLIENT.getGuildByID(Statics.ID_GUILD);
			guild = Statics.GUILD;
			LOGGER.debug("Bot joined guild: {}", guild.getName());
			Flip.init();
			new Timer().schedule(new MainTimer(), 5000, 60000);
			bInit = true;
			LOGGER.debug("Initialization done");
		}
		LOGGER.info("Bot ready");
	}

	@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {//TODO: sollte gesaved werden?
		LOGGER.warn("Bot disconnected");
		RelogTimer.addDC();
		new RelogTimer();
	}
	
	@EventSubscriber
	public synchronized void onMessageEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		if (DATABASE.containsUser(message.getAuthor())) {
			Commands.trigger(message);
		} else {
			LOGGER.warn("Typing user wasn't found in Database");
		}
	}
	
	@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {
		try {
			event.getUser().addRole(guild.getRolesByName("Newfags").get(0));
			LOGGER.info("added role(Newfags) to {}", event.getUser().getName());
		} catch(MissingPermissionsException | DiscordException | RateLimitException e) {
			LOGGER.error("Error while adding role to {}", event.getUser().getName(), e);
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
