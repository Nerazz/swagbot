package dbot.comm;

import static dbot.Poster.post;

import dbot.UserData;
import dbot.comm.items.Xpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.*;

final class Buy {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Buy");

	static void m(UserData dBuyer, String params) {
		Pattern pattern = Pattern.compile("([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		switch (matcher.group(1)) {
			case "xpot":
				if (matcher.group(3) == null) return;
				pattern = Pattern.compile("([a-z]+)");
				matcher = pattern.matcher(matcher.group(3));
				if (!matcher.matches()) return;
				new Xpot(dBuyer, matcher.group(1));
				break;

			case "reminder":
				int price = 100;
				int anzahl = 1;
				params = "" + matcher.group(3);
				pattern = pattern.compile("\\d+");
				matcher = pattern.matcher(params);
				if (matcher.matches()) anzahl = Integer.parseInt(matcher.group());
				if (dBuyer.getGems() < (price * anzahl)) {
					post(dBuyer.getName() + ", du hast zu wenig :gem:.");
				} else {
					dBuyer.subGems(price * anzahl);
					dBuyer.addReminder(anzahl);
					LOGGER.info("{} bought {} Reminder", dBuyer.getName(), anzahl);
					if (anzahl > 1) {
						post(dBuyer.getName() + ", hier sind deine " + anzahl + " Reminder.");
					} else {
						post(dBuyer.getName() + ", hier ist dein Reminder.");
					}
				}
				break;

			default:
				break;
			}
		}
}
