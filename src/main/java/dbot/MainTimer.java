package dbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;

import java.sql.*;
import java.util.List;
import java.util.*;

import static dbot.Poster.post;

class MainTimer extends TimerTask {//TODO: namen ändern
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.MainTimer");
	private static final Presences ONLINE = Presences.valueOf("ONLINE");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;
	private static final IGuild GUILD = Statics.GUILD;
	
	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;

	MainTimer() {
		BOT_CLIENT.changeStatus(Status.game("frisch online"));
		//System.out.println("constructed maintimer");
	}

	@Override
	public void run() {
		System.out.println("tick");
		minuteCount++;
		if ((minuteCount % 5) == 0) {
			if ((minuteCount % 60) == 0) {
				hourCount++;
				minuteCount = 0;

				if ((hourCount % 24) == 0) {
					dayCount++;
					hourCount = 0;//TODO: day++
					//SERVER_DATA.addDay();
				/*if ((SERVER_DATA.getDaysOnline() % 3) == 0) {
					DATABASE.save(true);
				}*/
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

		/*try (Connection con = SQLPool.getDataSource().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT `exp`, `level`, `swagLevel`, `swagPoints`, `potDuration`, `expRate` FROM `users` WHERE `id` = ?")) {
			List<IUser> userList = GUILD.getUsers();
			for (IUser user : userList) {
				ps.setString(1, user.getID());
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {//DB doesn't contain user
					System.out.println("adding");
					try (PreparedStatement psAdd = con.prepareStatement("INSERT INTO `users` (`id`, `name`) VALUES (?, ?)")) {
						psAdd.setString(1, user.getID());
						psAdd.setString(2, user.getName());
						psAdd.executeUpdate();
						con.commit();
					}
					rs = ps.executeQuery();
					rs.next();
				}
				int potDuration = rs.getInt("potDuration");
				if (user.getPresence() == ONLINE) {
					int exp = rs.getInt("exp");
					int level = rs.getInt("level");
					int swagLevel = rs.getInt("swagLevel");
					int swagPoints = rs.getInt("swagPoints");
					double expRate = rs.getDouble("expRate");
					int gems;
					if (swagLevel > 0) {
						gems = (int) Math.round(3.0 + swagPoints / 5.0 * (swagPoints / (swagPoints + 5.0) + 1.0));
					} else {
						gems = 3;
					}
					exp += (int)((Math.round(Math.random() * 3.0) + 4.0 + swagLevel) * expRate);
					if (exp >= getLevelThreshold(level)) {
						while (exp >= getLevelThreshold(level)) {
							exp -= getLevelThreshold(level);
							level++;
							post(":tada: DING! " + user.getName() + " ist Level " + level + "! :tada:");
							LOGGER.info("{} leveled to Level {}", user.getName(), level);
						}
						ps.executeUpdate("UPDATE `users` SET `level` = " + level + " WHERE `id` = " + user.getID());//TODO: mit if an anderes update anhängen (flaggen, wenn lvlup)
					}
					ps.executeUpdate("UPDATE `users` SET `gems` = `gems` + " + gems + ", `exp` = " + exp + " WHERE `id` = " + user.getID());
				}
				rs.close();
				if (potDuration > 0) {
					potDuration--;
					if (potDuration < 1) {
						ps.executeUpdate("UPDATE `users` SET `potDuration` = 0, `expRate` = 1.0 WHERE `id` = " + user.getID());
					} else {
						ps.executeUpdate("UPDATE `users` SET `potDuration` = " + potDuration + " WHERE `id` = " + user.getID());
					}
				}
			}
			con.commit();
		} catch(SQLException e) {
			e.printStackTrace();
		}*/

		List<IUser> userList = GUILD.getUsers();
		for (IUser user : userList) {
			UserData uData;//TODO: vor schleife für weniger overhead?
			if (user.getPresence() == ONLINE) {
				uData = new UserData(user, 255);//gems, exp, level, expRate, potDur, swagLevel, swagPoints, reminder
				if (uData.getSwagLevel() > 0) {
					double tmpPoints = (double)uData.getSwagPoints();
					uData.addGems((int)Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
				} else {
					uData.addGems(3);
				}
				//data.addExp((int)((Math.round(Math.random() * 3.0) + 4.0 + data.getSwagLevel()) * data.getExpRate()));
				int exp = (int)((Math.round(Math.random() * 3) + 4 + uData.getSwagLevel()) * uData.getExpRate()) / 1000;
				//System.out.println(user.getName() + " getting " + exp + "Exp");
				uData.addExp(exp);
			} else {
				uData = new UserData(user, 152);//expRate, potDur, reminder
			}
			uData.reducePotDuration();
			uData.update();
		}
		System.out.println("done");
	}

	//private void update(IUser user) {
		
		/*if (!DATABASE.containsUser(user)) {
			DATABASE.add(user);
			LOGGER.info("{} added to Database!", user.getName());
		}*/
		/*UserData userData = DATABASE.getData(user);
		userData.addExp((int)((Math.round(Math.random() * 3.0) + 4.0 + userData.getSwagLevel()) * userData.getExpRate()));
		if (userData.getSwagLevel() > 0) {
			double tmpPoints = (double)userData.getSwagPoints();
			userData.addGems((int)Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
		} else {
			userData.addGems(3);
		}
	}*/
}