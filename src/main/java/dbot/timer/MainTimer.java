package dbot.timer;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dbot.util.Poster.post;

public class MainTimer implements Runnable{//TODO: namen ändern
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.MainTimer");
	//private static final Presences ONLINE = Presences.valueOf("ONLINE");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;

	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;

	public MainTimer() {
		try {
			BOT_CLIENT.changeStatus(Status.game("frisch online"));
		} catch(Exception e) {
			e.printStackTrace();//TODO: log
		}
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

			List<String> idList = new ArrayList<>();
			List<IGuild> guildList = Statics.BOT_CLIENT.getGuilds();
			for (IGuild guild : guildList) {
				List<IUser> userList = guild.getUsers();
				for (IUser user : userList) {
					if (user.getPresence() == Presences.ONLINE) {
						idList.add(user.getID());//TODO: hier schon query bauen?
					}
				}
			}
			String lockQuery = 		"LOCK TABLES `users` WRITE";
			String freeQuery = 		"UNLOCK TABLES";
			String selectQuery =	"SELECT `id`, `gems`, `level`, `exp`, `swagLevel`, `swagPoints`, `reminder`, `expRate`, `potDur` " +
									"FROM `users` WHERE `id` IN (";
			for (String id : idList) {
				selectQuery += id + ", ";
			}
			selectQuery = selectQuery.substring(0, selectQuery.length() - 2) + ")";
			try(Connection con = SQLPool.getDataSource().getConnection(); Statement statement = con.createStatement(); PreparedStatement psSelect = con.prepareStatement(selectQuery)) {
				statement.execute(lockQuery);
				con.commit();
				ResultSet rs = psSelect.executeQuery();
				UserData userData;
				/*rs.next();
				System.out.println(rs.getString("id"));
				rs.beforeFirst();*/
				while (rs.next()) {
					//public UserData(String id, int gems, int level, int exp, int expRate, int potDur, int swagLevel, int swagPoints, int reminder) {
					userData = new UserData(rs.getString("id"), rs.getInt("gems"), rs.getInt("level"), rs.getInt("exp"), rs.getInt("expRate"), rs.getInt("potDur"), rs.getInt("swagLevel"), rs.getInt("swagPoints"), rs.getInt("reminder"));
					if (userData.getSwagLevel() > 0) {
						double tmpPoints = (double) userData.getSwagPoints();
						userData.addGems((int) Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
					} else {
						userData.addGems(3);
					}
					int exp = (int) ((Math.round(Math.random() * 3) + 4 + userData.getSwagLevel()) * userData.getExpRate()) / 1000;
					userData.addExp(exp);
					userData.reducePotDuration();
					String update = "UPDATE `users` SET `gems` = " + userData.getGems() + ", `level` = " + userData.getLevel() + ", `exp` = " + userData.getExp() + ", `reminder` = " + userData.getReminder() + ", `expRate` = " + userData.getExpRate() + ", `potDur` = " + userData.getPotDuration() + " WHERE `id` = " + userData.getId();//TODO: besser!!
					statement.addBatch(update);
				}
				rs.close();
				statement.executeBatch();
				statement.execute(freeQuery);//auch zum batch dazu?
				con.commit();
			} catch(SQLException e) {
				e.printStackTrace();//TODO: log
			}

			/*//"gems", "exp", "level", "expRate", "potDur", "swagLevel", "swagPoints", "reminder"
			String query = "UPDATE `users` SET `gems` = `gems` + ?, `exp` = `exp` + ?, `level` = `level` + ? WHERE `id` = ?";//TODO: auf jeden fall besser machen
			try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query);
				PreparedStatement psLock = con.prepareStatement(lockQuery); PreparedStatement psFree = con.prepareStatement(freeQuery)) {

				for (int ref = 0; ref < Statics.GUILD_LIST.size(); ref++) {//TODO: eigenen iterator schreiben für GuildMap
					IGuild guild = Statics.GUILD_LIST.getGuild(ref);
					if (guild == null) continue;
					//UserData.addUsers(guild.getUsers(), ref);
					UserData uData;
					for (IUser user : guild.getUsers()) {//TODO: ref rausnehmen bei UserData
						uData = new UserData(user, 255);//gems, exp, level, expRate, potDur, swagLevel, swagPoints, reminder
						if (user.getPresence() == Presences.ONLINE) {
							if (uData.getSwagLevel() > 0) {
								double tmpPoints = (double) uData.getSwagPoints();
								uData.addGems((int) Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
							} else {
								uData.addGems(3);
							}
							//data.addExp((int)((Math.round(Math.random() * 3.0) + 4.0 + data.getSwagLevel()) * data.getExpRate()));
							int exp = (int) ((Math.round(Math.random() * 3) + 4 + uData.getSwagLevel()) * uData.getExpRate()) / 1000;
							//System.out.println(user.getName() + " getting " + exp + "Exp");
							uData.addExp(exp);
						}
						uData.reducePotDuration();
						//uData.update();

					}
				}
			}*/
			System.out.println("done");
		}catch(Exception e) {
			e.printStackTrace();//TODO: log
		}
	}
}