package dbot.comm;

import static dbot.Poster.post;

import dbot.Database;
import dbot.Statics;
import dbot.UserData;

import java.util.regex.*;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

final class Give {

	private Give() {}
	
	static void m(UserData dGiver, String params) {
		Pattern pattern = Pattern.compile("^<@(\\d+)>\\s(\\d+)");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		IUser uGetter = Statics.GUILD.getUserByID(matcher.group(1));
		if (uGetter == null) return;//wird eigentlich schon in getData abgefangen
		if (uGetter.getPresence().equals(Presences.OFFLINE)) {
			System.out.println("User nicht online");
			return;
		}
		UserData dGetter = Database.getInstance().getData(uGetter);
		if (dGetter == null) return;
		int gems = Integer.parseInt(matcher.group(2));
		if ((dGiver.getGems() < gems) || (gems < 1)) {
			System.out.println("fail");
			return;
		}
		dGiver.subGems(gems);
		dGetter.addGems(gems);
		post(uGetter + " bekommt " + gems + " :gem: von " + dGiver.getUser());
		System.out.println(dGiver.getName() + " gave " + uGetter.getName() + gems + " gems");

	}
	
}
