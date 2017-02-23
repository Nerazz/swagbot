package dbot;

import dbot.comm.Commands;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import dbot.timer.MainTimer;
import dbot.util.GuildList;
import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.*;
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
import java.util.EnumSet;
import java.util.concurrent.*;

class Events {//TODO: IListener benutzen
	private final static Logger LOGGER = LoggerFactory.getLogger("dbot.Events");

	Events() {}

	@EventSubscriber
	public synchronized void onGuildCreateEvent(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		IGuild guild = event.getGuild();
		LOGGER.info("Bot joined {}", guild.getName());
		if (!Statics.BOT_CLIENT.isReady()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();//TODO: log
			}
		}

		String query = "SELECT `ref`, `botChannel` FROM `guilds` WHERE `id` = ?";//TODO: geht bestimmt besser, wird zweimal abgerufen
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, guild.getID());
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				rs = getGuildRef(con, ps, guild);
			}
			int ref = rs.getInt("ref");
			Statics.GUILD_LIST.addGuild(ref, guild, guild.getChannelByID(rs.getString("botChannel")));//TODO: vielleicht besser machbar
			rs.close();
		} catch(SQLException e) {
			LOGGER.error("SQL or Future failed in onGuildCreateEvent", e);
		}
		//TODO: addUsers() von userdata
	}

	private static ResultSet getGuildRef(Connection con, PreparedStatement ps, IGuild guild) {
		ResultSet rs = null;
		LOGGER.info("GUILD {} NOT FOUND, ADDING GUILD TO DATABASE", guild.getName());
		String insertQuery = "INSERT INTO `guilds` (`id`, `name`, `botChannel`) VALUES (?, ?, ?)";
		try (PreparedStatement psInsertGuild = con.prepareStatement(insertQuery)) {
			psInsertGuild.setString(1, guild.getID());
			psInsertGuild.setString(2, guild.getName());
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
				} catch (MissingPermissionsException | DiscordException e) {
					e.printStackTrace();//TODO: log
				}
				return null;
			});
			psInsertGuild.setString(3, fID.get());
			System.out.println(Statics.BOT_CLIENT.isReady());
			psInsertGuild.executeUpdate();
			con.commit();//hier ref erstellt
			rs = ps.executeQuery();
			rs.next();
			int ref = rs.getInt("ref");

			String createQuery = "CREATE TABLE `guild" + ref + "` (" +
					"`id` varchar(128) COLLATE utf8_bin NOT NULL," +
					"`name` varchar(128) COLLATE utf8_bin NOT NULL, " +
					"PRIMARY KEY (`id`), " +
					"UNIQUE KEY `id_UNIQUE` (`id`), " +
					"KEY `id_INDEX` (`id`), " +
					"KEY `name_INDEX` (`name`), " +
					"CONSTRAINT `FKEY_id_G" + ref + "` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE, " +
					"CONSTRAINT `FKEY_name_G" + ref + "` FOREIGN KEY (`name`) REFERENCES `users` (`name`) ON DELETE CASCADE ON UPDATE CASCADE" +
					") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

			try(PreparedStatement psCreateGuild = con.prepareStatement(createQuery)) {
				psCreateGuild.executeUpdate();
				con.commit();
			}
			//LOGGER.info("Init for new Server ({}) done.", guild.getName());
		} catch(SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();//TODO: log
		}
		return rs;
	}

	@EventSubscriber
	public synchronized void onReadyEvent(ReadyEvent event) {
		LOGGER.info("BotReadyEvent");
		notifyAll();
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		MainTimer mainTimer = new MainTimer();
		try {
			executor.scheduleAtFixedRate(mainTimer, 5, 10, TimeUnit.SECONDS);//TODO: care, 60sec
		} catch(Exception e) {
			e.printStackTrace();//TODO: log
		}
	}

	@EventSubscriber
	public void onDisconnectedEvent(DisconnectedEvent event) {
		LOGGER.warn("Bot disconnected due to {}", event.getReason());
	}
	
	@EventSubscriber
	public synchronized void onMessageEvent(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		Commands.trigger(message);
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
