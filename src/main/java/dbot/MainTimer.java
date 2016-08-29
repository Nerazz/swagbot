package dbot;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;
import java.util.List;
import java.util.*;

/*
event für tick?

punkte	was tun
1		idle
2		online
3		online + voice

*/

class MainTimer extends TimerTask {//TODO: name ändern
	
	private final Presences ONLINE = Presences.valueOf("ONLINE");
	//private final Presences IDLE = Presences.valueOf("IDLE");
	private static final IDiscordClient botClient = Statics.BOT_CLIENT;
	private static final IGuild guild = Statics.GUILD;
	private static final DataBase DB = Events.getDB();
	private static final ServerData SD = Events.getSD();
	
	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;

	MainTimer() {
		botClient.changeStatus(Status.game("frisch online"));
	}


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
					SD.addDay();
					if ((SD.getDaysOnline() % 3) == 0) {
						DB.save(true);
					}
				}
			}

			if (dayCount != 0) {
				botClient.changeStatus(Status.game("seit " + dayCount + "d " + hourCount + "h online"));
			} else if (hourCount != 0) {
				botClient.changeStatus(Status.game("seit " + hourCount + "h " + minuteCount + "m online"));
			} else {
				botClient.changeStatus(Status.game("seit " + minuteCount + "m online"));
			}

			if (((hourCount % 5) == 0) && (minuteCount == 0)) {
				DB.save(false);
				System.out.println("DataBase durch MainTimer gesaved");
			}
		}

		List<IUser> userList = guild.getUsers();
		for(IUser user: userList) {
			if (!user.getID().equals(Statics.ID_BOT)) {
				if (user.getPresence() == ONLINE) {
					update(user, 3);
				} /*else if (user.getPresence() == IDLE) {
					update(user, 0);
				}*/
				if (DB.containsUser(user)) {
					DB.getData(user).reducePotDuration();
				}
			}

		}
	}

	private void update(IUser user, int p) {//TODO: bei playerjoin event wird liste aktualisiert, nicht bei jedem update
		
		if (!DB.containsUser(user)) {//OPTIMIEREN (DOUBLE-CHECK!!)
			DB.add(user);
			System.out.println("----------------------------------");
			System.out.println(user.getName() + " added to DB!");
			System.out.println("----------------------------------");
		}
		if (p > 0) {
			//DB.getData(u).addExp((p + DB.getData(u).getPresLevel() * 2) + (int)(Math.random() * 10));//aendern, dass idle groessere auswirkungen hat
			DB.getData(user).addExp((int) (((p * (Math.random() * 3.0)) / 2.0 + (p + 1.0 + DB.getData(user).getPresLevel())) * DB.getData(user).getExpRate()));
			DB.getData(user).addGems(p * (DB.getData(user).getPresLevel() + 1));//nochmal fixen!!!
		}
	}
	
}

















