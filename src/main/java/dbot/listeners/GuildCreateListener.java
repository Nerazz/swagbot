package dbot.listeners;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Niklas on 23.02.2017.
 */
public final class GuildCreateListener implements IListener<GuildCreateEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.GuildCreateListener");
	static final Object LOCK = new Object();

	@Override
	public void handle(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		IGuild guild = event.getGuild();
		LOGGER.info("Bot joined {}", guild.getName());
		if (!Statics.BOT_CLIENT.isReady()) {
			synchronized (LOCK) {
				try {
					LOCK.wait();
				} catch (InterruptedException e) {
					LOGGER.error("LOCK interrupted:", e);
				}
			}
		}

		String query = "SELECT `ref`, `botChannel` FROM `guilds` WHERE `id` = ?";//TODO: geht bestimmt besser, wird zweimal abgerufen
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, guild.getID());
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				rs.close();//zum releasen oder einfach überschreiben ok?
				rs = getGuildRef(con, ps, guild);
			}
			int ref = rs.getInt("ref");
			IChannel channel = guild.getChannelByID(rs.getString("botChannel"));
			Statics.GUILD_LIST.addGuild(ref, guild, channel);//TODO: vielleicht besser machbar
			System.out.println(Statics.GUILD_LIST.toString());
			rs.close();
			UserData.addUsers(guild.getUsers(), ref);

			createPost(channel);
		} catch(SQLException e) {
			LOGGER.error("SQL or Future failed in onGuildCreateEvent", e);
		}
	}

	private static ResultSet getGuildRef(Connection con, PreparedStatement ps, IGuild guild) {
		ResultSet rs = null;
		LOGGER.info("GUILD {} NOT FOUND, ADDING GUILD TO DATABASE", guild.getName());
		String insertQuery = "INSERT INTO `guilds` (`id`, `name`, `botChannel`) VALUES (?, ?, ?)";
		try (PreparedStatement psInsertGuild = con.prepareStatement(insertQuery)) {
			psInsertGuild.setString(1, guild.getID());
			psInsertGuild.setString(2, guild.getName());
			LOGGER.info("creating botChannel on {}", guild.getName());
			Future<String> fID = RequestBuffer.request(() -> {//TODO: erst nach permission createn (bei neuem server fail)
				try {
					IChannel botChannel = guild.createChannel("botspam");
					EnumSet<Permissions> allPerms = EnumSet.allOf(Permissions.class);
					EnumSet<Permissions> nonePerms = EnumSet.noneOf(Permissions.class);
					EnumSet<Permissions> addPerms = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES, Permissions.READ_MESSAGE_HISTORY);
					EnumSet<Permissions> remPerms = EnumSet.of(Permissions.MANAGE_CHANNEL, Permissions.MANAGE_PERMISSIONS, Permissions.MANAGE_MESSAGES);
					botChannel.overrideUserPermissions(Statics.BOT_CLIENT.getOurUser(), allPerms, nonePerms);
					botChannel.overrideRolePermissions(guild.getEveryoneRole(), addPerms, remPerms);
					botChannel.changeTopic(":robot: !commands :robot:");
					return botChannel.getID();
				} catch (MissingPermissionsException | DiscordException e) {
					LOGGER.error("future failed:", e);
				}
				return null;
			});
			psInsertGuild.setString(3, fID.get());
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
			//TODO: add guild.getUsers() to users + guildREF
			//LOGGER.info("Init for new Server ({}) done.", guild.getName());
		} catch(SQLException | InterruptedException | ExecutionException e) {
			LOGGER.error("SQL or future failed:", e);
		}
		return rs;
	}

	public static void createPost(IChannel channel) {
		Future<IMessage> fMessage = Poster.post("Offene Flip-Räume:```xl\nkeine```", channel, -1);
		try {
			Statics.POST_LIST.add(fMessage.get());
		} catch(InterruptedException | ExecutionException e) {
			LOGGER.error("future failed:", e);
		}
	}
}
