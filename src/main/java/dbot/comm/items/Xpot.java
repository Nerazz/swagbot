package dbot.comm.items;

import static dbot.util.Poster.post;

import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Niklas on 17.08.2016.
 */
public class Xpot {//Buy extenden oder ähnliches?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Xpot");

	public Xpot(IUser user, String pot, IChannel channel) {
		switch(pot) {
			case "tall":
				use(user, 70, 1500, 500, channel);
				break;
			case "grande":
				use(user, 65, 2000, 1000, channel);
				break;
			case "venti":
				use(user, 60, 3000, 2000, channel);
				break;
			case "giant":
				use(user, 120, 5000, 9999, channel);
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
				use (user, 10, mix, 10000, channel);
				break;
			default:
				break;
		}
	}

	private int rnd(int mult) {
		return (int)(Math.round(Math.random() * mult));
	}

	private void use(IUser user, int duration, int amp, int price, IChannel channel) {//duration in ticks
		UserData data = new UserData(user, 25);//gems, expRate, potDur
		if (data.getGems() < price) {
			post(user + ", du hast zu wenig :gem:.", channel);
		}else if (data.getPotDuration() > 0) {
			post(user + ", letzter XPot(x" + data.getExpRate() / 1000 + ") ist noch für " + data.getPotDuration() + " min aktiv.", channel);//TODO: formatierung
		} else {
			data.subGems(price);
			data.setExpRate(amp);
			data.setPotDur(duration);
			post(user + ", hier ist dein XPot (x" + amp / 1000 + ") für " + duration + " min!", channel);//TODO: formatieung
			LOGGER.info("{} -> XPot for {} (x{})", user.getName(), price, amp);
			data.update();
		}
	}
}
