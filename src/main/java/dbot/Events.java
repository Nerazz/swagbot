package dbot;

import dbot.comm.Commands;
import dbot.comm.Flip;
import dbot.timer.RelogTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

class Events {
	private final static Logger LOGGER = LoggerFactory.getLogger("dbot.Events");
	//private static boolean bInit = false;
	//private static IGuild guild;
	//private static IGuild[] guilds;
	//private static ArrayList<IGuild> guildList = new ArrayList<>();
	private static DataMap<String, Integer> guildMap = new DataMap<>();
	//private static final Database DATABASE = Database.getInstance();

	Events() {}
	
	/*@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		if (!bInit) {
			Statics.GUILD = Statics.BOT_CLIENT.getGuildByID(Statics.ID_GUILD);
			guild = Statics.GUILD;
			LOGGER.debug("Bot joined guild: {}", guild.getName());
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new MainTimer(), 10 * 1000, 60 * 1000);//TODO: care
			bInit = true;
			LOGGER.debug("Initialization done");
		}
		LOGGER.info("Bot ready");
	}*/

	@EventSubscriber
	public synchronized void onGuildCreateEvent(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		IGuild guild = event.getGuild();
		LOGGER.info("Bot joined {}", guild.getName());
		try {
			wait();
		}catch(InterruptedException e) {
			e.printStackTrace();//TODO: log
		}
		String query = "SELECT `ref` FROM `guilds` WHERE `id` = ?";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, guild.getID());
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				LOGGER.info("GUILD {} NOT FOUND, ADDING GUILD TO DATABASE", guild.getName());
				try(PreparedStatement psAdd = con.prepareStatement("INSERT INTO `guilds` (`id`, `botChannel`) VALUES (?, ?)")) {
					psAdd.setString(1, guild.getID());
					LOGGER.info("creating botChannel on {}", guild.getName());
					Future<String> fID = RequestBuffer.request(() -> {
						try {
							IChannel botChannel = guild.createChannel("testchannel");
							EnumSet<Permissions> allPerms = EnumSet.allOf(Permissions.class);
							EnumSet<Permissions> nonePerms = EnumSet.noneOf(Permissions.class);
							EnumSet<Permissions> addPerms = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES, Permissions.READ_MESSAGE_HISTORY);
							EnumSet<Permissions> remPerms = EnumSet.of(Permissions.MANAGE_CHANNEL, Permissions.MANAGE_PERMISSIONS, Permissions.MANAGE_MESSAGES);
							botChannel.overrideUserPermissions(Statics.BOT_CLIENT.getOurUser(), allPerms, nonePerms);
							botChannel.overrideRolePermissions(guild.getEveryoneRole(), addPerms, remPerms);
							botChannel.changeTopic("swag");
							return botChannel.getID();
						}catch(MissingPermissionsException | DiscordException e) {
							e.printStackTrace();//TODO: log
						}
						return null;
					});
					psAdd.setString(2, fID.get());
					System.out.println(Statics.BOT_CLIENT.isReady());
					psAdd.executeUpdate();
					con.commit();
					LOGGER.info("Init for new Server ({}) done.", guild.getName());
				}
				rs = ps.executeQuery();
				rs.next();
			}
			guildMap.put(guild.getID(), rs.getInt("ref"));
		} catch(SQLException | InterruptedException | ExecutionException e) {
			LOGGER.error("SQL or Future failed in onGuildCreateEvent", e);
		}
	}

	@EventSubscriber
	public synchronized void onReadyEvent(ReadyEvent event) {
		LOGGER.info("BotReadyEvent");
		notifyAll();
	}

	/*return RequestBuffer.request(() -> {
		try {
			IPrivateChannel privateChannel = user.getOrCreatePMChannel();
			return new MessageBuilder(bClient).withChannel(privateChannel).withContent(s).build();
		} catch(MissingPermissionsException | DiscordException e) {
			LOGGER.error("failed posting private(to {}): {}", user.getName(), s, e);
		}
		return null;
	});*/

	@EventSubscriber
	public void onDisconnectedEvent(DisconnectedEvent event) {
		LOGGER.warn("Bot disconnected due to {}", event.getReason());
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
			event.getUser().addRole(event.getGuild().getRolesByName("Newfags").get(0));
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
