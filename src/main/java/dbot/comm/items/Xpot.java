package dbot.comm.items;

import static dbot.Poster.post;

import dbot.SQLPool;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Niklas on 17.08.2016.
 */
public class Xpot {//Buy extenden oder ähnliches?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Xpot");

	public Xpot(IUser user, String pot) {
		switch(pot) {
			case "tall":
				use(user, 70, 1500, 500);
				break;
			case "grande":
				use(user, 65, 2000, 1000);
				break;
			case "venti":
				use(user, 60, 3000, 2000);
				break;
			case "giant":
				use(user, 120, 5000, 9999);
				break;
			case "unstable":
				double rnd = Math.random();
				int mix;
				if (rnd < 0.05) {
					mix = rnd(1000);
				}else if (rnd < 0.15) {
					mix = rnd(4000) + 1000;
				}else if (rnd < 0.55) {
					mix = rnd(45000) + 5000;
				}else if (rnd < 0.95) {
					mix = rnd(100000) + 50000;
				}else {
					mix = 200000;
				}
				use (user, 10, mix, 10000);
				break;
			default:
				break;
		}
	}

	private int rnd(int mult) {
		return (int)(Math.round(Math.random() * mult));
	}

	private void use(IUser user, int duration, int amp, int price) {//duration in ticks
		UserData data = new UserData(user, 25);//gems, expRate, potDur
		if (data.getGems() < price) {
			post(user + ", du hast zu wenig :gem:.");
		}else if (data.getPotDuration() > 0) {
			post(user + ", letzter XPot(x" + data.getExpRate() / 1000 + ") ist noch für " + data.getPotDuration() + " min aktiv.");
		} else {
			data.subGems(price);
			data.setExpRate(amp);
			data.setPotDuration(duration);
			post(user + ", hier ist dein XPot (x" + amp / 1000 + ") für " + duration + " min!");
			LOGGER.info("{} -> XPot for {} (x{})", user.getName(), price, amp);
			data.update();
		}
	}
}
