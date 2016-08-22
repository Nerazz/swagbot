package dbot.comm;

import dbot.UserData;
import java.util.regex.*;
import dbot.comm.items.Xpot;

final class Buy {
	
	static void m(UserData dBuyer, String params) {
		Pattern pattern = Pattern.compile("([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
				
		switch (matcher.group(1)) {
			case "xpot":
				if (matcher.group(3) == null) return;
				pattern = Pattern.compile("([a-z]+)");
				matcher = pattern.matcher(matcher.group(3));
				if (!matcher.matches()) return;
				new Xpot(dBuyer, matcher.group(1));

			default:
				break;
			}
		}
}
