package dbot.comm;

import static dbot.util.Poster.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import java.util.regex.*;

final class Roll {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Roll");
	private static final String desc = "testDesc";

	static void m(IMessage message) {
		IChannel channel = message.getChannel();
		IUser author = message.getAuthor();
		String content = message.getContent().toLowerCase();
		Matcher matcher = Pattern.compile("^!roll(?:\\s(\\d+))?(?:\\s(\\d+))?$").matcher(content);
		if (!matcher.matches()) {
			LOGGER.error("Matcher matcht nicht!({})", content);
			return;
		}
		String g1 = matcher.group(1);
		String g2 = matcher.group(2);

		if (g1 == null) {//keine parameter
			int rnd = (int)(Math.random() * 100) + 1;
			if (rnd != 100) {
				post(":game_die: " + author + " hat eine " + rnd + " gewürfelt! :game_die:", channel);
			}
			else {
				post(":slot_machine: " + author + " hat eine :100: gewürfelt!!! :slot_machine:\ngz :ok_hand:", channel);
			}
		} else {//g1 != null
			int first = Integer.parseInt(g1);
			if (first < 1) {
				post("Es sind nur Zahlen > 0 erlaubt", channel);
				return;
			}
			if (g2 == null) {
				int rnd = (int)(Math.random() * first) + 1;
				post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " gewürfelt! :game_die:", channel);
			} else {//g2 != null
				int second = Integer.parseInt(g2);
				if (second < 1) {
					post("Es sind nur Zahlen > 0 erlaubt", channel);
				} else if (first > second) {
					post("Die erste Zahl sollte größer als die zweite sein", channel);
				} else {
					int rnd = (int)(Math.random() * (second - first + 1)) + first;
					post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " - " + second + " gewürfelt! :game_die:", channel);
				}
			}
		}
	}

	String getDesc() {
		return desc;
	}
}
