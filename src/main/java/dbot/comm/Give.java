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

final class Give {//TODO: genauer angucken, scheint schlecht; funktioniert nicht auf mainserver
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

	/*static void m(UserData dGiver, String params) {
		Pattern pattern = Pattern.compile("^<@(\\d+)>\\s(\\d+)$");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		IUser uGetter = Statics.GUILD.getUserByID(matcher.group(1));
		if (uGetter == null) return;//TODO: wird eigentlich schon in getData abgefangen?
		if (uGetter.getPresence().equals(Presences.OFFLINE)) {
			LOGGER.warn("User not online");
			return;
		}
		UserData dGetter = Database.getInstance().getData(uGetter);
		if (dGetter == null) return;
		int gems = Integer.parseInt(matcher.group(2));
		if ((dGiver.getGems() < gems) || (gems < 1)) {
			LOGGER.warn("{} tried giving {} Gems", dGiver.getName(), gems);
			return;
		}
		dGiver.subGems(gems);
		dGetter.addGems(gems);
		post(uGetter + " bekommt " + gems + " :gem: von " + dGiver.getUser());
		LOGGER.info("{} gave {} {} Gems", dGiver.getName(), uGetter.getName(), gems);
	}*/
}
