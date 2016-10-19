package dbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;
import java.util.List;
import java.util.*;

class MainTimer implements Runnable {//TODO: namen ändern
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.MainTimer");
	private static final Presences ONLINE = Presences.valueOf("ONLINE");
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;
	private static final IGuild GUILD = Statics.GUILD;
	private static final Database DATABASE = Database.getInstance();
	private static final ServerData SERVER_DATA = DATABASE.getServerData();
	
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
					hourCount = 0;
					SERVER_DATA.addDay();
					if ((SERVER_DATA.getDaysOnline() % 3) == 0) {
						DATABASE.save(true);
					}
				}
			}

			if (dayCount != 0) {
				BOT_CLIENT.changeStatus(Status.game("seit " + dayCount + "d " + hourCount + "h online"));
			} else if (hourCount != 0) {
				BOT_CLIENT.changeStatus(Status.game("seit " + hourCount + "h " + minuteCount + "m online"));
			} else {
				BOT_CLIENT.changeStatus(Status.game("seit " + minuteCount + "m online"));
			}

			if (((hourCount % 5) == 0) && (minuteCount == 0)) {
				DATABASE.save(false);
				LOGGER.info("Database saved from MainTimer");
			}
		}

		List<IUser> userList = GUILD.getUsers();
		for(IUser user: userList) {
			if (!user.getID().equals(Statics.ID_BOT)) {
				if (user.getPresence() == ONLINE) update(user);
				if (DATABASE.containsUser(user)) {
					DATABASE.getData(user).reducePotDuration();
				}
			}
		}
	}

	private void update(IUser user) {
		
		if (!DATABASE.containsUser(user)) {//TODO: OPTIMIEREN (DOUBLE-CHECK!!), vielleicht alle user, egal ob online oder nicht, in db laden?
			DATABASE.add(user);
			LOGGER.info("{} added to Database!", user.getName());
		}
		UserData userData = DATABASE.getData(user);
		userData.addExp((int)((Math.round(Math.random() * 3.0) + 4.0 + userData.getSwagLevel()) * userData.getExpRate()));
		if (userData.getSwagLevel() > 0) {
			double tmpPoints = (double)userData.getSwagPoints();
			userData.addGems((int)Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
		} else {
			userData.addGems(3);
		}
	}
}