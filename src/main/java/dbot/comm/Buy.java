package dbot.comm;

import static dbot.Poster.post;

import dbot.SQLPool;
import dbot.UserData;
import dbot.comm.items.Xpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.sql.*;
import java.util.regex.*;

final class Buy {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Buy");

	static void m(IUser buyer, String params) {
		Pattern pattern = Pattern.compile("([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		switch (matcher.group(1)) {
			case "xpot":
				if (matcher.group(3) == null) return;
				pattern = Pattern.compile("([a-z]+)");
				matcher = pattern.matcher(matcher.group(3));
				if (!matcher.matches()) return;
				new Xpot(buyer, matcher.group(1));
				break;

			case "reminder":
				int price = 100;
				int anzahl = 1;
				params = "" + matcher.group(3);
				pattern = pattern.compile("\\d+");
				matcher = pattern.matcher(params);
				if (matcher.matches()) anzahl = Integer.parseInt(matcher.group());


				try(Connection conn = SQLPool.getDataSource().getConnection(); Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {//TODO: welcher type?
					ResultSet rs = statement.executeQuery("SELECT `gems`, `reminder` FROM `users` WHERE `id` = " + buyer.getID());
					int gems = rs.getInt("gems");
					if (gems < (price * anzahl)) {//TODO: "gems" oder 1?
						post(buyer.getName() + ", du hast zu wenig :gem:.");
					} else {
						rs.updateInt("gems", gems - (price * anzahl));
						rs.updateInt("reminder", rs.getInt("reminder") + anzahl);
						LOGGER.info("{} bought {} Reminder", buyer.getName(), anzahl);

						if (anzahl > 1) {
							post(buyer.getName() + ", hier sind deine " + anzahl + " Reminder.");
						} else {
							post(buyer.getName() + ", hier ist dein Reminder.");
						}
					}
				} catch(SQLException e) {
					e.printStackTrace();
				}



				break;

			default:
				break;
			}
		}
}
