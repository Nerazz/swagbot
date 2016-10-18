package dbot.comm.items;

import static dbot.Poster.post;

import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 17.08.2016.
 */
public class Xpot {//Buy extenden oder ähnliches?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Xpot");

	public Xpot(UserData userData, String pot) {
		switch(pot) {
			case "tall":
				use(userData, 70, 1.5, 500);
				break;
			case "grande":
				use(userData, 65, 2.0, 1000);
				break;
			case "venti":
				use(userData, 60, 3.0, 2000);
				break;
			case "giant":
				use(userData, 120, 5.0, 9999);
				break;
			case "unstable":
				use (userData, 10, Math.round(Math.random() * 90.0 + 10.0), 10000);
				break;
			default:
				break;
		}
	}

	private void use(UserData userData, int duration, double amp, int price) {
		if (userData.getGems() < price) {//TODO: get price von json
			post(userData.getName() + ", du hast zu wenig :gem:");
		} else if (userData.getExpRate() > 1.0) {
			post(userData.getName() + ", letzter XPot ist noch für " + userData.getPotDuration() + " min aktiv.");
		} else {
			userData.subGems(price);
			post(userData.getName() + ", hier ist dein XPot (x" + amp + ") for " + duration + " min!");
			LOGGER.info("{} -> XPot für {} (x{})", userData.getName(), price, amp);
			//System.out.println(userData.getName() + " -> xpot für " + price + "(x" + amp +")");
			userData.setExpRate(amp);
			userData.setPotDuration(duration);
		}
	}
}
