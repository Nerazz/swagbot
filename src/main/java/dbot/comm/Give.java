package dbot.comm;

import static dbot.util.Poster.post;

import dbot.sql.UserData;

import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

final class Give {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Give");

	static void m(IMessage message) {
		IUser author = message.getAuthor();
		String params = message.getContent().toLowerCase();
		IChannel channel = message.getChannel();
		Pattern pattern = Pattern.compile("^<@\\d+>\\s(\\d+)$");//TODO: besser machen (siehe roll)
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) {
			post("Falsches Format, auch an das @ gedacht?", channel);
			return;
		}

		UserData dGiver = new UserData(author, 1);//gems
		UserData dGetter = new UserData(message.getMentions().get(0), 1);//gems

		if (!matcher.matches()) return;
		int gems = Integer.parseInt(matcher.group(1));
		if ((dGiver.getGems() < gems) || (gems < 1)) {
			post("Hört sich nach keinem guten Plan an...", channel);
			LOGGER.warn("{} tried giving {} {} Gems", dGiver.getName(), dGetter.getName(), gems);
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
