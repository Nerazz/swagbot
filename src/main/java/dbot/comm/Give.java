package dbot.comm;

import dbot.Statics;
import dbot.UserData;
import dbot.DataBase;
import static dbot.Poster.post;
import java.util.regex.*;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

final class Give {
	
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
		UserData dGetter = new DataBase().getData(uGetter);
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

		/*String sGems = "";

		try {
			int gems = Integer.parseInt(sGems);
			if ((dGiver.getGems() < gems) || (gems < 1)) {
				System.out.println("fail");
				return;
			}
			dGiver.subGems(gems);
			dGetter.addGems(gems);
			System.out.println("gave " + gems + " gems.");
		} catch(Exception e) {
			System.out.println("parseerror Give.m");
		}*/


		/*case "give":
			try {
			//System.out.println("give start");
			String test = param2.substring(param2.indexOf('<') + 2, param2.indexOf('>'));
			System.out.println(test);
			IUser ugetter = guild.getUserByID(test);
			if (ugetter.getPresence() == Presences.valueOf("ONLINE")) {
				UserData getter = DB.getData(ugetter);
				Give.m(DB.getData(author), param1, getter);
				pos.post("hat geklappt");
				System.out.println("gave gems");
			}
			else {
				System.out.println("error");
			}
		} catch(Exception e) {
			System.out.println("give error");
		}
		break;*/

	}
	
}
