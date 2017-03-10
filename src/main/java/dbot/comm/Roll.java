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

	static void main(IMessage message) {
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
				post(String.format(":game_die: %s rolled %d! :game_die:", author, rnd), channel);
				//post(":game_die: " + author + " rolled " + rnd + "! :game_die:", channel);
			}
			else {
				post(String.format("slot_machine: %s rolled :100:!!! :slot_machine:%n:ok_hand: :ok_hand: :ok_hand: ", author), channel);
				//post(":slot_machine: " + author + " hat eine :100: gewürfelt!!! :slot_machine:\ngz :ok_hand:", channel);
			}
		} else {//g1 != null
			int first = Integer.parseInt(g1);
			if (first < 1) {
				post("Given number is < 1", channel);
				return;
			}
			if (g2 == null) {
				int rnd = (int)(Math.random() * first) + 1;
				post(String.format(":game_die: %s rolled %d out of %d! :game_die:", author, rnd, first), channel);
				//post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " gewürfelt! :game_die:", channel);
			} else {//g2 != null
				int second = Integer.parseInt(g2);
				if (second < 1) {
					post("Given number is < 1", channel);
				} else if (first > second) {
					post("The first number has to be smaller then the second.", channel);
				} else {
					int rnd = (int)(Math.random() * (second - first + 1)) + first;
					post(String.format(":game_die: %s rolled %d out of %d - %d! :game_die:", author, rnd, first, second), channel);
					//post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " - " + second + " gewürfelt! :game_die:", channel);
				}
			}
		}
	}

	String getDesc() {
		return desc;
	}
}
