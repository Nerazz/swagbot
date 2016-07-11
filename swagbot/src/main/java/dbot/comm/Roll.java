package dbot.comm;

import dbot.Poster;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import sx.blah.discord.api.IDiscordClient;
import java.util.regex.*;

public final class Roll {
	public static void m(Poster pos, IUser author, String params) {
		Pattern pattern = Pattern.compile("(\\d+)(\\s(\\d+))?");
		Matcher matcher = pattern.matcher(params);
		
		if (matcher.matches()) {
			int first = Integer.parseInt(matcher.group(1));
			if (matcher.group(3) != null) {
				int second = Integer.parseInt(matcher.group(3));
				if ((second > 0) && (first <= second)) {
					int rnd = (int)(Math.random() * (second - first + 1)) + first;
					pos.post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " - " + second + " gewürfelt! :game_die:");
				}
			} else {
				if (first > 0) {
					int rnd = (int)(Math.random() * first) + 1;
					pos.post(":game_die: " + author + " hat eine " + rnd + " aus " + first + " gewürfelt! :game_die:");
				}
			}
		} else {
			int rnd = (int)(Math.random() * 100) + 1;
			if (rnd != 100) {
				pos.post(":game_die: " + author + " hat eine " + rnd + " gewürfelt! :game_die:");
			}
			else {
				pos.post(":slot_machine: " + author + " hat eine :100: gewürfelt!!! :slot_machine:\ngz :ok_hand:");
			}
		}
	}
}
