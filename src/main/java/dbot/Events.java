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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

class Events {
	private final static Logger LOGGER = LoggerFactory.getLogger("dbot.Events");
	private static boolean bInit = false;
	private static IGuild guild;
	//private static final Database DATABASE = Database.getInstance();

	Events() {}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		if (!bInit) {
			Statics.GUILD = Statics.BOT_CLIENT.getGuildByID(Statics.ID_GUILD);
			guild = Statics.GUILD;
			LOGGER.debug("Bot joined guild: {}", guild.getName());
			//DATABASE.load();
			//final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		/*final ThreadFactory factory = new ThreadFactory() {

				@Override
				public Thread newThread(Runnable target) {
					final Thread thread = new Thread(target);
					LOGGER.debug("Creating new worker thread");
					thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

						@Override
						public void uncaughtException(Thread t, Throwable e) {
							LOGGER.error("Uncaught Exception", e);
						}
					});
					return thread;
				}
			};*/


			//final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(factory);
			//final MainTimer mainTimer = new MainTimer();
			//scheduler.scheduleAtFixedRate(new MainTimer(), 5, 5, SECONDS);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new MainTimer(), 10000, 60000);//TODO: care
			bInit = true;
			LOGGER.debug("Initialization done");
		}
		LOGGER.info("Bot ready");
	}

	@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {
		LOGGER.warn("Bot disconnected due to {}", event.getReason());
		RelogTimer.addDC();
		new RelogTimer();
	}
	
	@EventSubscriber
	public synchronized void onMessageEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		Commands.trigger(message);
		/*if (DATABASE.containsUser(message.getAuthor())) {TODO: fix mit db
			Commands.trigger(message);
		} else {
			LOGGER.warn("Typing user wasn't found in Database");
		}*/
	}
	
	@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {
		try {
			event.getUser().addRole(guild.getRolesByName("Newfags").get(0));
			LOGGER.info("added role(Newfags) to {}", event.getUser().getName());
			Poster.post(	"Willkommen auf dem nicesten Discord-Server ever :)" +
							"\nWenn du Lust hast, schau doch mal im #botspam vorbei, hier kann man ne nice Runde gamblen und co :)" +
							"\nZusätzlich solltest du #botspam auf @mention stellen (oder muten)" +
							"\nBei Fragen am Besten an @DPD oder @Stammboys wenden.", event.getUser());
		} catch(MissingPermissionsException | DiscordException | RateLimitException e) {
			LOGGER.error("Error while adding role to {} (or couldn't send message)", event.getUser().getName(), e);
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
