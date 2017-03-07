package dbot.comm;

import static dbot.util.Poster.post;

import dbot.sql.UserData;
import dbot.comm.items.Xpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.*;

final class Buy {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Buy");

	static void m(IMessage message) {
		IUser buyer = message.getAuthor();
		String params = message.getContent().toLowerCase();
		IChannel channel = message.getChannel();

		Pattern pattern = Pattern.compile("([a-z]+)(\\s(.+))?");//TODO: besser machen (siehe roll)
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		switch (matcher.group(1)) {
			case "xpot":
				if (matcher.group(3) == null) return;
				pattern = Pattern.compile("([a-z]+)");
				matcher = pattern.matcher(matcher.group(3));
				if (!matcher.matches()) return;
				new Xpot(buyer, matcher.group(1), channel);
				break;

			case "reminder":
				int price = 100;
				int anzahl = 1;
				params = "" + matcher.group(3);
				pattern = Pattern.compile("\\d+");//sollte negative abfangen
				matcher = pattern.matcher(params);
				if (matcher.matches()) anzahl = Integer.parseInt(matcher.group());

				UserData data = new UserData(buyer, 129);//gems, reminder
				if (data.getGems() < (price * anzahl)) {
					post(buyer + ", du hast zu wenig :gem:.", channel);
					return;
				}
				data.subGems(price * anzahl);
				data.addReminder(anzahl);
				LOGGER.info("{} bought {} Reminder", buyer.getName(), anzahl);
				if (anzahl > 1) {
					post(buyer + ", hier sind deine " + anzahl + " Reminder!", channel);
				} else {
					post(buyer + ", hier ist dein Reminder!", channel);
				}
				data.update();
				break;

			default:
				break;
			}
		}
}
