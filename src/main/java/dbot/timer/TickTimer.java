package dbot.timer;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.impl.UserDataImpl;
import dbot.util.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MainTimer, handles bot status (online) text and constant user updates for gems and exp
 *
 * @author Niklas Zd
 */
public final class TickTimer implements Runnable {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.TickTimer");
	/** bot client */
	private static final IDiscordClient BOT_CLIENT = Statics.BOT_CLIENT;

	/** minutes since bot start */
	private static int minutesOnline = 0;
	/** hours since bot start */
	private static int hoursOnline = 0;
	/** days since bot start */
	private static int daysOnline = 0;

	/**
	 * sets status text
	 */
	public TickTimer() {
		BOT_CLIENT.changePlayingText("fresh online");
	}

	/**
	 * handles updates on online timer and users
	 */
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
			List<Long> onlineUserIds = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).map(IUser::getLongID).collect(Collectors.toList());


			Map<Long, IUser> onlineUsers = Statics.BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).collect(Collectors.toMap(IUser::getLongID, u -> u));
			System.out.println(onlineUsers.size() + " users online");

			//TODO: add exp & gems, update users (mit UserDataImpl.update(List<UserDataImpl>))


			/*if (userDataImpl.getSwagLevel() > 0) {
				double tmpPoints = (double) userDataImpl.getSwagPoints();
				userDataImpl.addGems((int) Math.round(3.0 + tmpPoints / 5.0 * (tmpPoints / (tmpPoints + 5.0) + 1.0)));
			} else {
				userDataImpl.addGems(3);
			}
			int exp = (int) ((Math.round(Math.random() * 3) + 4 + userDataImpl.getSwagLevel()) * userDataImpl.getExpRate()) / 1000;
			userDataImpl.addExp(exp);
			userDataImpl.reducePotDur();*/
			for (Map.Entry<Long, IUser> e : onlineUsers.entrySet()) {
				UserDataImpl ud =  UserCache.getUserData(e.getValue());
				ud.addGems(3);
				ud.addExp(5);
				ud.update();
			}

			UserCache.cleanNotAccessed();//TODO: nur jede Stunde oder so
			System.out.println("done");
		} catch(Exception e) {
			LOGGER.error("Exception in TickTimer:", e);//TODO: notwendig hier? testen (exception wird auch um Timer schon gefangen!)
		}
	}
}