package dbot;

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


public class MainTimer extends Events implements Runnable {
	
	private final Presences ONLINE = Presences.valueOf("ONLINE");
	private final Presences IDLE = Presences.valueOf("IDLE");
	
	private static int minuteCount	= 0;
	private static int hourCount	= 0;
	private static int dayCount		= 0;
	
	private List<IUser> lUser = new ArrayList<IUser>();
	private IUser user;
	
	
	protected MainTimer() {
		Thread tMainTimer = new Thread(this, "MainTimer Thread");
		System.out.println("created: " + tMainTimer);
		botClient.changeStatus(Status.game("frisch online"));
		
		//deFile();
		
		tMainTimer.start();
	}


	public void run() {
		// TODO: relativer path
		
		try {
			while (true) {
				Thread.sleep(60000);//60000 gute zeit
				minuteCount += 1;
				if ((minuteCount % 5) == 0) {
					if ((minuteCount % 60) == 0) {
						hourCount += 1;
						minuteCount = 0;
						
						if ((hourCount % 24) == 0) {
							dayCount += 1;
							hourCount = 0;
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
						DB.save();
						System.out.println("DataBase durch MainTimer gesaved");
					}
				}
				
				lUser = guild.getUsers();
				int countO = 0;
				int countI = 0;
				
				for (int i = 0; i < lUser.size(); i++) {
					user = lUser.get(i);
					if (!user.getID().equals(Statics.ID_BOT)) {
						if (user.getPresence() == ONLINE) {
							update(user, 3);
							countO += 1;
						}
						/*else if (user.getPresence() == IDLE) {
							update(user, 1);
							countI += 1;
						}*/
						DB.getData(user).reducePotDuration();
					}
					
				}
				//System.out.print("<" + countO + " Online, " + countI + " Idle>");
			}
		} catch(Exception e) {
		}
	}

	private void update(IUser u, int p) {//TODO: u durch user ersetzen; bei playerjoin event wird liste aktualisiert, nicht bei jedem update
		
		
		if (DB.containsUser(u)) {//optimieren!!!(doublecheck)
			//DB.getData(u).addExp((p + DB.getData(u).getPresLevel() * 2) + (int)(Math.random() * 10));//aendern, dass idle groessere auswirkungen hat
			DB.getData(u).addExp((int)(((p * (Math.random() * 3.0)) / 2.0 + (p + 1.0 + DB.getData(u).getPresLevel())) * DB.getData(u).getExpRate()));
			DB.getData(u).addGems(p * (DB.getData(u).getPresLevel() + 1));//nochmal fixen!!!
		}
		else {
			DB.add(u);
			System.out.println("----------------------------------");
			System.out.println(u.getName() + " added to DB!");
			System.out.println("----------------------------------");
		}
		
	}
	
}

















