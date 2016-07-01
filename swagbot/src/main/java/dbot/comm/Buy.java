package dbot.comm;

import dbot.UserData;
import sx.blah.discord.handle.obj.IUser;

import dbot.Poster;
import dbot.timer.ExpTimer;

public final class Buy {
	
	public static void one(UserData dBuyer, String item) {
		
	}
	
	public static void two(UserData dBuyer, String item, String type) {
		Poster pos = new Poster();
		int g = dBuyer.getGems();
		IUser uBuyer = dBuyer.getUser();
		switch (item) {//switch item
			case "xpot":
				switch (type) {//switch type
					case "tall":
						if (g < 500) {//get price von json
							pos.post(uBuyer + ", du hast zu wenig :gem:");
						}
						else if (dBuyer.getExpRate() > 1.0) {
							pos.post(uBuyer + ", Boost ist schon aktiv du Noob");
						}
						else {
							dBuyer.subGems(500);
							pos.post(uBuyer + ", hier ist dein xpot tall!");
							System.out.println(uBuyer.getName() + " -> xpot 1");
							new ExpTimer(dBuyer, 1.5, 4200000);//abzaehlen bei timer (-= 1)
						}
						break;
					case "grande":
						if (g < 1000) {
							pos.post(uBuyer + ", du hast zu wenig :gem:");
						}
						else if (dBuyer.getExpRate() > 1.0) {
							pos.post(uBuyer + ", Boost ist schon aktiv du Noob");
						}
						else {
							dBuyer.subGems(1000);
							pos.post(uBuyer + ", hier ist dein xpot grande!");
							System.out.println(uBuyer.getName() + " -> xpot 2");
							new ExpTimer(dBuyer, 2, 3900000);//abzaehlen bei timer (-= 1)
						}
						break;
					case "venti":
						if (g < 2000) {
							pos.post(uBuyer + ", du hast zu wenig :gem:");
						}
						else if (dBuyer.getExpRate() > 1.0) {
							pos.post(uBuyer + ", Boost ist schon aktiv du Noob");
						}
						else {
							dBuyer.subGems(2000);
							pos.post(uBuyer + ", hier ist dein xpot venti!");
							System.out.println(uBuyer.getName() + " -> xpot 3");
							new ExpTimer(dBuyer, 3, 3600000);//abzaehlen bei timer (-= 1)
						}
						break;
						
					default:
						break;
				}//switch /type
			default:
				break;
			}//switch /item
		}
}
