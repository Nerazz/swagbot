package dbot.comm.items;

import dbot.sql.UserData;
import dbot.sql.impl.UserDataImpl;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dbot.util.Poster.post;

/**
 * Created by Niklas on 07.03.2017.
 */
public final class Reminder {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Reminder");
	private static final int PRICE = 100;
	private static final int ANZAHL = 1;

	public static void main(IMessage message) {
		String content = message.getContent().toLowerCase();
		Matcher matcher = Pattern.compile("^!buy\\sreminder(?:\\s(\\d+))?").matcher(content);
		if (!matcher.lookingAt()) {
			LOGGER.error("Matcher matcht nicht!({})", content);
			return;
		}
		int anzahl;
		if (matcher.group(2) != null) {
			anzahl = Integer.parseInt(matcher.group());
		} else {
			anzahl = ANZAHL;
		}
		IUser buyer = message.getAuthor();
		UserData data = new UserDataImpl(buyer, 129);//gems, reminder
		IChannel channel = message.getChannel();
		if (data.getGems() < (PRICE * anzahl)) {
			post(buyer + ", you don't have enough :gem:.", channel);
			return;
		}
		data.subGems(PRICE * anzahl);
		data.addReminder(anzahl);
		LOGGER.info("{} bought {} Reminder", buyer.getName(), anzahl);
		if (anzahl > 1) {
			post(buyer + ", here are your " + anzahl + " reminder!", channel);
		} else {
			post(buyer + ", here's your reminder!", channel);
		}
		data.update();
	}
}
