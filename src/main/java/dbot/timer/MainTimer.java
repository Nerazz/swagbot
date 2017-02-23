package dbot.timer;

import dbot.Statics;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;

import java.util.List;

import static dbot.util.Poster.post;

public class MainTimer implements Runnable{//TODO: namen ändern
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.MainTimer");
	private static final Presences ONLINE = Presences.valueOf("ONLINE");
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

			/*for (int ref = 0; ref < Statics.GUILD_LIST.size(); ref++) {
				IGuild guild = Statics.GUILD_LIST.getGuild(ref);
				if (guild == null) continue;
				UserData.addUsers(guild.getUsers(), ref);
			}*/

			/*for (int ref = 0; ref < Statics.GUILD_LIST.size(); ref++) {//TODO: eigenen iterator schreiben für GuildMap
				IGuild guild = Statics.GUILD_LIST.getGuild(ref);
				if (guild == null) continue;
				List<IUser> userList = guild.getUsers();
				//int ref = Statics.GUILD_LIST.getRef(guild);
				for (IUser user : userList) {
					UserData uData;//TODO: vor schleife für weniger overhead?
					if (user.getPresence() == ONLINE) {
						uData = new UserData(user, ref, 255);//gems, exp, level, expRate, potDur, swagLevel, swagPoints, reminder
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
					} else {
						uData = new UserData(user, ref, 152);//expRate, potDur, reminder
					}
					uData.reducePotDuration();
					uData.update();
				}
			}*/
			System.out.println("done");
		}catch(Exception e) {
			e.printStackTrace();//TODO: log
		}
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