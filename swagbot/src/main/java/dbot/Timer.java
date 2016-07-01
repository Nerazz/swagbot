package dbot;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

import java.util.List;
import java.util.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;


/*
event für tick?

punkte	was tun
1		idle
2		online
3		online + voice

*/


public class Timer extends Events implements Runnable {
	
	private final Presences ONLINE = Presences.valueOf("ONLINE");
	private final Presences IDLE = Presences.valueOf("IDLE");
	//private final String botID = "183234453122973697";//MAIN
	//private final String botID = "189453076111949824";//TEST
	
	private List<IUser> lUser = new ArrayList<IUser>();
	private IUser user;
	
	
	Timer() {
		Thread tTimer = new Thread(this, "Timer Thread");
		System.out.println("created: " + tTimer);
		bClient.updatePresence(false, Optional.of("frisch online"));
		
		//deFile();
		
		tTimer.start();
	}


	public void run() {
		// TODO: relativer path
		
		try {
			while (true) {
				Thread.sleep(60000);//60000 gute zeit
				minuteStat += 1;
				if ((minuteStat % 60) == 0) {
					hourStat += 1;
				}
				if (((hourStat % 3) == 0) && ((minuteStat % 60) == 0)) {
					DB.save();
					System.out.println("wrote to file!!");
				}
				if (hourStat != 0) {
					bClient.updatePresence(false, Optional.of("seit " + hourStat + "h " + (minuteStat % 60) + "m online"));
				}
				else {
					bClient.updatePresence(false, Optional.of("seit " + minuteStat + "m online"));
				}
				
				lUser = guild.getUsers();
				
				
				int countO = 0;
				int countI = 0;
					
					
				for (int i = 0; i < lUser.size(); i++) {
					user = lUser.get(i);
					if (!user.getID().equals(idBot)) {
						if (user.getPresence() == ONLINE) {
							update(user, 3);
							countO += 1;
						}
						//else if (user.getPresence() == IDLE) {
						//	update(user, 1);
						//	countI += 1;
						//}
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

















