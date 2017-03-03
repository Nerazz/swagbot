package dbot.timer;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;

import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

public final class MainTimer implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.MainTimer");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;

	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;

	public MainTimer() {
		BOT_CLIENT.changeStatus(Status.game("frisch online"));
	}

	@Override
	public void run() {
		System.out.println("tick");
		try {
			minuteCount++;
			if ((minuteCount % 5) == 0) {
				if ((minuteCount % 60) == 0) {
					hourCount++;
					minuteCount = 0;

					if ((hourCount % 24) == 0) {
						dayCount++;
						hourCount = 0;//TODO: day++
						//SERVER_DATA.addDay();
					}
				}

				if (dayCount != 0) {
					BOT_CLIENT.changeStatus(Status.game("seit " + dayCount + "d " + hourCount + "h online"));
				} else if (hourCount != 0) {
					BOT_CLIENT.changeStatus(Status.game("seit " + hourCount + "h " + minuteCount + "m online"));
				} else {
					BOT_CLIENT.changeStatus(Status.game("seit " + minuteCount + "m online"));
				}
			}

			Map<String, IUser> onlineUsers = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().equals(Presences.ONLINE)).collect(Collectors.toMap(IUser::getID, u -> u));
			System.out.println(onlineUsers.size() + " users online");
			String lockQuery = 		"LOCK TABLES `users` WRITE";
			String freeQuery = 		"UNLOCK TABLES";
			String selectQuery =	"SELECT * FROM `users` WHERE `id` IN (";//TODO: nur die relevanten Infos selecten?
			String updateQuery =	"UPDATE `users` SET `gems` = ?, `level` = ?, `exp` = ?, `reminder` = ?, `expRate` = ?, `potDur` = ? WHERE `id` = ?";
			for (Map.Entry<String, IUser> entry : onlineUsers.entrySet()) {
				selectQuery += entry.getKey() + ", ";
			}
			selectQuery = selectQuery.substring(0, selectQuery.length() - 2) + ")";
			try(Connection con = SQLPool.getDataSource().getConnection(); Statement statement = con.createStatement(); PreparedStatement psSelect = con.prepareStatement(selectQuery); PreparedStatement psUpdate = con.prepareStatement(updateQuery)) {
				statement.execute(lockQuery);
				con.commit();
				ResultSet rs = psSelect.executeQuery();
				UserData userData;

				while (rs.next()) {
					userData = new UserData(onlineUsers.get(rs.getString("id")), 0);//nur empty user anlegen; user von idKey getten
					userData.setGems(rs.getInt("gems"));//TODO: vielleicht besser?
					userData.setLevel(rs.getInt("level"));
					userData.setExp(rs.getInt("exp"));
					//userData.setSwagLevel(rs.getInt("swagLevel"));
					//userData.setSwagPoints(rs.getInt("swagPoints"));
					userData.setReminder(rs.getInt("reminder"));
					userData.setExpRate(rs.getInt("expRate"));
					userData.setPotDur(rs.getInt("potDur"));

					if (userData.getSwagLevel() > 0) {
						double tmpPoints = (double) userData.getSwagPoints();
						userData.addGems((int) Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
					} else {
						userData.addGems(3);
					}
					int exp = (int) ((Math.round(Math.random() * 3) + 4 + userData.getSwagLevel()) * userData.getExpRate()) / 1000;
					userData.addExp(exp);
					userData.reducePotDuration();
					psUpdate.setInt(1, userData.getGems());
					psUpdate.setInt(2, userData.getLevel());
					psUpdate.setInt(3, userData.getExp());
					psUpdate.setInt(4, userData.getReminder());
					psUpdate.setInt(5, userData.getExp());
					psUpdate.setInt(6, userData.getPotDuration());
					psUpdate.setString(7, userData.getId());

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
			LOGGER.error("Exception in MainTimer:", e);//TODO: notwendig hier? testen (exception wird auch um Timer schon gefangen!)
		}
	}
}