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

class MainTimer implements Runnable {//TODO: namen ändern
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.MainTimer");
	private static final Presences ONLINE = Presences.valueOf("ONLINE");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;
	private static final IGuild GUILD = Statics.GUILD;
	//private static final Database DATABASE = Database.getInstance();
	//private static final ServerData SERVER_DATA = DATABASE.getServerData();
	
	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;

	MainTimer() {
		BOT_CLIENT.changeStatus(Status.game("frisch online"));
	}

	@Override
	public void run() {
		System.out.println("tick");
		minuteCount += 1;
		if ((minuteCount % 5) == 0) {
			if ((minuteCount % 60) == 0) {
				hourCount += 1;
				minuteCount = 0;

				if ((hourCount % 24) == 0) {
					dayCount += 1;
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

			/*if (((hourCount % 5) == 0) && (minuteCount == 0)) {
				DATABASE.save(false);
				LOGGER.info("Database saved from MainTimer");
			}*/
		}

		/*try(Connection conn = SQLPool.getDataSource().getConnection(); Statement statement = conn.createStatement()) {//TODO: welcher type?
			ResultSet rs = statement.executeQuery("SELECT `id`, `gems`, `exp` FROM `users`");
			while(rs.next()) {
				String id =
				if (!user.get)
			}
			//String query = "UPDATE `users` SET WHERE `id` = " + user.getID();
			rs.next();
			int gems = rs.getInt("gems");
			if (gems < price) {//TODO: "gems" oder 1?
			} else if (rs.getDouble("expRate") > 1.0) {
			} else {
				statement.executeUpdate("UPDATE `users` SET `gems` = gems - " + price + " WHERE `id` = " + user.getID());
				//TODO: Rest updaten


				rs.updateInt("gems", gems - price);
				LOGGER.info("{} -> XPot for {} (x{})", user.getName(), price, amp);
				rs.updateDouble("expRate", amp);
				rs.updateInt("potDuration", duration);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}*/


		try(Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement("SELECT `gems`, `exp`, `swagLevel`, `swagPoints` FROM `users` WHERE `id` = ?")) {//TODO: welcher type?

		List<IUser> userList = GUILD.getUsers();
		for(IUser user: userList) {
			if (!user.getID().equals(Statics.ID_BOT)) {
				if (user.getPresence() == ONLINE) {
					statement.setString(1, user.getID());
					ResultSet rs = statement.executeQuery();
					rs.next();
					int exp = rs.getInt("exp");
					int gems = rs.getInt("gems");
					int swagLevel = rs.getInt("swagLevel");
					int swagPoints = rs.getInt("swagPoints");
					rs.close();
					exp += (int)((Math.round(Math.random() * 3.0) + 4.0 + 0));//TODO: swaglevel dazu!!!
					gems += 3;
					//System.out.println(user.getName() + ": " + gems + "; " + exp);
					//statement = conn.prepareStatement("UPDATE `users` SET `gems` = " + gems + ", `exp` = " + exp + " WHERE `id` = "+ user.getID());
					statement.executeUpdate("UPDATE `users` SET `gems` = " + gems + ", `exp` = " + exp + " WHERE `id` = "+ user.getID());

					//ps.executeUpdate();
					//ps.close();
					//statement.executeUpdate("UPDATE `users` SET `gems` = " + gems + ", `exp` = " + exp + " WHERE `id` = "+ user.getID());//TODO: mit ? machen und batchupdate
				}
				/*if (DATABASE.containsUser(user)) {TODO:!!!
					DATABASE.getData(user).reducePotDuration();
				}*/
			}
		}
		conn.commit();
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}

	//private void update(IUser user) {
		
		/*if (!DATABASE.containsUser(user)) {//TODO: OPTIMIEREN (DOUBLE-CHECK!!), vielleicht alle user, egal ob online oder nicht, in db laden?
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