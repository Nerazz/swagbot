package dbot.timer;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.impl.UserDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TickTimer implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.TickTimer");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;

	private static int minutesOnline = 0;
	private static int hoursOnline = 0;
	private static int daysOnline = 0;

	public TickTimer() {
		BOT_CLIENT.changePlayingText("fresh online");
	}

	@Override
	public void run() {
		System.out.println("tick");
		try {
			minutesOnline++;
			if ((minutesOnline % 5) == 0) {
				if ((minutesOnline % 60) == 0) {
					hoursOnline++;
					minutesOnline = 0;

					if ((hoursOnline % 24) == 0) {
						daysOnline++;
						hoursOnline = 0;//TODO: day++
						//SERVER_DATA.addDay();
					}
				}

				if (daysOnline != 0) {
					BOT_CLIENT.changePlayingText(String.format("for %dd %dh online", daysOnline, hoursOnline));
				} else if (hoursOnline != 0) {
					BOT_CLIENT.changePlayingText(String.format("for %dh %dm online", hoursOnline, minutesOnline));
				} else {
					BOT_CLIENT.changePlayingText(String.format("for %dm online", minutesOnline));
				}
			}

			//Map<String, IUser> onlineUsers = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).collect(Collectors.toMap(IUser::getID, u -> u));
			List<String> onlineUsers = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).map(IUser::getID).collect(Collectors.toList());
			onlineUsers.removeAll(UserDataImpl.getCachedIds());
			UserDataImpl.addUsersToCache(onlineUsers);
			Map<String, UserDataImpl> userCache = UserDataImpl.getUserCache();





			Map<String, IUser> onlineUsers = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).collect(Collectors.toMap(IUser::getID, u -> u));
			System.out.println(onlineUsers.size() + " users online");
			String lockQuery = 		"LOCK TABLES `users` WRITE";
			String freeQuery = 		"UNLOCK TABLES";
			String selectQuery =	"SELECT * FROM `users` WHERE `id` IN (";//TODO: nur die relevanten Infos selecten!!
			String updateQuery =	"UPDATE `users` SET `gems` = ?, `level` = ?, `exp` = ?, `reminder` = ?, `expRate` = ?, `potDur` = ? WHERE `id` = ?";
			for (Map.Entry<String, IUser> entry : onlineUsers.entrySet()) {
				selectQuery += entry.getKey() + ", ";
			}
			selectQuery = selectQuery.substring(0, selectQuery.length() - 2) + ")";
			try(Connection con = SQLPool.getDataSource().getConnection(); Statement statement = con.createStatement(); PreparedStatement psSelect = con.prepareStatement(selectQuery); PreparedStatement psUpdate = con.prepareStatement(updateQuery)) {
				statement.execute(lockQuery);
				con.commit();
				ResultSet rs = psSelect.executeQuery();
				UserDataImpl userDataImpl;//TODO: interface nutzen!

				while (rs.next()) {
					userDataImpl = new UserDataImpl(onlineUsers.get(rs.getString("id")), 0);//nur empty user anlegen; user von idKey getten
					userDataImpl.setGems(rs.getInt("gems"));//TODO: vielleicht besser?
					userDataImpl.setLevel(rs.getInt("level"));
					userDataImpl.setExp(rs.getInt("exp"));
					//userDataImpl.setSwagLevel(rs.getInt("swagLevel"));
					//userDataImpl.setSwagPoints(rs.getInt("swagPoints"));
					userDataImpl.setReminder(rs.getInt("reminder"));
					userDataImpl.setExpRate(rs.getInt("expRate"));
					userDataImpl.setPotDur(rs.getInt("potDur"));

					if (userDataImpl.getSwagLevel() > 0) {
						double tmpPoints = (double) userDataImpl.getSwagPoints();
						userDataImpl.addGems((int) Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
					} else {
						userDataImpl.addGems(3);
					}
					int exp = (int) ((Math.round(Math.random() * 3) + 4 + userDataImpl.getSwagLevel()) * userDataImpl.getExpRate()) / 1000;
					userDataImpl.addExp(exp);
					userDataImpl.reducePotDur();
					psUpdate.setInt(1, userDataImpl.getGems());
					psUpdate.setInt(2, userDataImpl.getLevel());
					psUpdate.setInt(3, userDataImpl.getExp());
					psUpdate.setInt(4, userDataImpl.getReminder());
					psUpdate.setInt(5, userDataImpl.getExpRate());
					psUpdate.setInt(6, userDataImpl.getPotDur());
					psUpdate.setString(7, userDataImpl.getId());

					psUpdate.addBatch();
				}
				rs.close();
				int[] count = psUpdate.executeBatch();
				System.out.println("updated " + count.length + " users");//TODO: wieviele wirklich? ist nur die Anzahl versuchter Updates...
				statement.execute(freeQuery);
				con.commit();
			} catch(SQLException e) {
				LOGGER.error("userUpdate failed:", e);
			}
			System.out.println("done");
		} catch(Exception e) {
			LOGGER.error("Exception in TickTimer:", e);//TODO: notwendig hier? testen (exception wird auch um Timer schon gefangen!)
		}
	}
}