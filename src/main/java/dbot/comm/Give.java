package dbot.comm;

import static dbot.Poster.post;

import dbot.Database;
import dbot.Statics;
import dbot.UserData;

import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

final class Give {//TODO: genauer angucken, scheint schlecht
	private static final Logger logger = LoggerFactory.getLogger("dbot.comm.Give");

	static void m(UserData dGiver, String params) {
		System.out.println("give start");
		Pattern pattern = Pattern.compile("^<@(\\d+)>\\s(\\d+)");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		System.out.println("give 2");
		IUser uGetter = Statics.GUILD.getUserByID(matcher.group(1));
		if (uGetter == null) return;//TODO: wird eigentlich schon in getData abgefangen?
		System.out.println("give 3");
		if (uGetter.getPresence().equals(Presences.OFFLINE)) {
			logger.warn("User not online");
			return;
		}
		UserData dGetter = Database.getInstance().getData(uGetter);
		if (dGetter == null) return;
		System.out.println("give 4");
		int gems = Integer.parseInt(matcher.group(2));
		if ((dGiver.getGems() < gems) || (gems < 1)) {
			logger.warn("{} tried giving {} Gems", dGiver.getName(), gems);
			return;
		}
		dGiver.subGems(gems);
		dGetter.addGems(gems);
		post(uGetter + " bekommt " + gems + " :gem: von " + dGiver.getUser());
		logger.info("{} gave {} {} Gems", dGiver.getName(), uGetter.getName(), gems);
	}
}
