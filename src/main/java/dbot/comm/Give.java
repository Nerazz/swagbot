package dbot.comm;

import static dbot.util.Poster.post;

import dbot.Statics;
import dbot.sql.UserData;

import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

final class Give {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Give");

	static void m(UserData dGiver, UserData dGetter, String params, IChannel channel) {
		Pattern pattern = Pattern.compile("^<@\\d+>\\s(\\d+)$");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		int gems = Integer.parseInt(matcher.group(1));
		if ((dGiver.getGems() < gems) || (gems < 1)) {
			LOGGER.warn("{} tried giving {} Gems", dGiver.getName(), gems);
			return;
		}
		dGiver.subGems(gems);
		dGetter.addGems(gems);
		dGiver.update();
		dGetter.update();
		post(dGetter.getUser() + " bekommt " + gems + " :gem: von " + dGiver.getUser(), channel);
		LOGGER.info("{} gave {} {} Gems", dGiver.getName(), dGetter.getName(), gems);
	}
}
