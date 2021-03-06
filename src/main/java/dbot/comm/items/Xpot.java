package dbot.comm.items;

import static dbot.util.Poster.post;

import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Niklas on 17.08.2016.
 */
public final class Xpot {//Buy extenden oder ähnliches?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Xpot");

	//public static void main(IUser user, String pot, IChannel channel) {
	public static void main(IMessage message) {
		String content = message.getContent().toLowerCase();
		UserData uData = new UserData(message.getAuthor(), 25);//gems, expRate, potDuration
		IChannel channel = message.getChannel();

		Matcher matcher = Pattern.compile("^!buy\\s[a-z]+\\s([a-z]+)").matcher(content);
		if (!matcher.lookingAt()) {
			LOGGER.error("Matcher matcht nicht!({})", content);
			return;
		}

		switch(matcher.group(1)) {
			case "tall":
				use(uData, 70, 1500, 500, channel);
				break;
			case "grande":
				use(uData, 65, 2000, 1000, channel);
				break;
			case "venti":
				use(uData, 60, 3000, 2000, channel);
				break;
			case "giant":
				use(uData, 120, 5000, 9999, channel);
				break;
			case "unstable":
				double rnd = Math.random();
				int mix;
				if (rnd < 0.05) {
					mix = rnd(1000);
				}else if (rnd < 0.15) {
					mix = rnd(4000) + 1000;
				}else if (rnd < 0.55) {
					mix = rnd(45000) + 5000;
				}else if (rnd < 0.95) {
					mix = rnd(100000) + 50000;
				}else {
					mix = 200000;
				}
				use (uData, 10, mix, 10000, channel);
				break;
			default:
				break;
		}
	}

	private static int rnd(int mult) {
		return (int)(Math.round(Math.random() * mult));
	}

	private static void use(UserData uData, int duration, int amp, int price, IChannel channel) {//duration in ticks
		//UserData uData = new UserData(user, 25);//gems, expRate, potDur
		IUser user = uData.getUser();
		if (uData.getGems() < price) {
			post(user + ", you don't have enough :gem:.", channel);
		}else if (uData.getPotDuration() > 0) {
			//post(String.format("%nBoost(x%s) ist noch %d min aktiv", uData.getFormattedExpRate(), uData.getPotDuration()), channel);
			post(String.format("%s, your boost(x%s) is still active for another %d min", uData.getName(), uData.getFormattedExpRate(), uData.getPotDuration()), channel);
			//post(user + ", letzter XPot(x" + uData.getExpRate() / 1000 + ") ist noch für " + uData.getPotDuration() + " min aktiv.", channel);//TODO: formatierung
		} else {
			uData.subGems(price);
			uData.setExpRate(amp);
			uData.setPotDur(duration);
			post(String.format(user + ", here's your XPot(x%s) which lasts %d min!", uData.getFormattedExpRate(), uData.getPotDuration()), channel);
			//post(user + ", hier ist dein XPot (x" + amp / 1000 + ") für " + duration + " min!", channel);//TODO: formatieung
			LOGGER.info("{} -> XPot for {} (x{})", user.getName(), price, amp);
			uData.update();
		}
	}
}
