package dbot.comm;

import static dbot.util.Poster.post;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import java.util.regex.*;

final class Roll {

	static void m(IUser author, IChannel channel, String params) {
		Pattern pattern = Pattern.compile("(\\d+)(\\s(\\d+))?");
		Matcher matcher = pattern.matcher(params);
		
		if (matcher.matches()) {
			int first = Integer.parseInt(matcher.group(1));
			if (matcher.group(3) != null) {
				int second = Integer.parseInt(matcher.group(3));
				if ((second > 0) && (first <= second)) {
					int rnd = (int)(Math.random() * (second - first + 1)) + first;
					post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " - " + second + " gewürfelt! :game_die:", channel);
				}
			} else {
				if (first > 0) {
					int rnd = (int)(Math.random() * first) + 1;
					post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " gewürfelt! :game_die:", channel);
				}
			}
		} else {
			int rnd = (int)(Math.random() * 100) + 1;
			if (rnd != 100) {
				post(":game_die: " + author + " hat eine " + rnd + " gewürfelt! :game_die:", channel);
			}
			else {
				post(":slot_machine: " + author + " hat eine :100: gewürfelt!!! :slot_machine:\ngz :ok_hand:", channel);
			}
		}
	}
}
