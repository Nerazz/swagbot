package dbot.timer;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;

import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;

import java.util.List;
import java.util.*;


/*public class RouleTimer implements Runnable {
	
	public static List<IUser> lRouleUser = new ArrayList<IUser>(); //private mit setter + getter machen
	public static IChannel RouleChannel;
	public static List<Integer> lInts = new ArrayList<Integer>();
	
	RouleTimer(IUser uRouleUser) {
		lRouleUser.add(uRouleUser);
		Thread tRouleTimer = new Thread(this, "RouleTimer Thread");
		System.out.println("created: " + tRouleTimer);
		
		tRouleTimer.start();
	}
	


	public void run() {
		try {
			Thread.sleep(20000);
			System.out.println("dabei waren: " + lRouleUser);
			int erg = (int)(Math.random() * 37);
			System.out.println("erg: " + erg);
			/*for (int i = 0; i < lInts.size(); i++) {
				if (lInts.get(i) == erg) {
					System.out.println("roule IF");
					ml.bMes(bClient, RouleChannel, "gz, " + lRouleUser.get(i) + ", du hast gewonnen :)");
				}
				else {
					System.out.println("roule ELSE");
					ml.bMes(bClient, RouleChannel, "Leider hat niemand gewonnen :(");
				}
			}
			
			lRouleUser.clear();
		/*} catch(MissingPermissionsException e) {
			e.printStackTrace();
		} catch(RateLimitException e) {
			e.printStackTrace();
		} catch(DiscordException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addRoule(IUser uRouleUser) {
		lRouleUser.add(uRouleUser);
		System.out.println("added " + uRouleUser);
	}
}*/
