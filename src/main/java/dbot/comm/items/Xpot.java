package dbot.comm.items;

import static dbot.Poster.post;

import dbot.SQLPool;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Niklas on 17.08.2016.
 */
public class Xpot {//Buy extenden oder ähnliches?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.items.Xpot");

	public Xpot(IUser user, String pot) {
		switch(pot) {
			case "tall":
				use(user, 70, 1.5, 500);
				break;
			case "grande":
				use(user, 65, 2.0, 1000);
				break;
			case "venti":
				use(user, 60, 3.0, 2000);
				break;
			case "giant":
				use(user, 120, 5.0, 9999);
				break;
			case "unstable":
				use (user, 10, Math.round(Math.random() * 90.0 + 10.0), 10000);
				break;
			default:
				break;
		}
	}

	private void use(IUser user, int duration, double amp, int price) {
		try(Connection conn = SQLPool.getDataSource().getConnection(); Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {//TODO: welcher type?
			ResultSet rs = statement.executeQuery("SELECT `gems`, `expRate`, `potDuration` FROM `users` WHERE `id` = " + user.getID());
			//String query = "UPDATE `users` SET WHERE `id` = " + user.getID();
			rs.next();
			int gems = rs.getInt("gems");
			if (gems < price) {//TODO: "gems" oder 1?
				post(user.getName() + ", du hast zu wenig :gem:.");
			} else if (rs.getDouble("expRate") > 1.0) {
				post(user.getName() + ", letzter XPot ist noch für " + rs.getInt("potDuration") + " min aktiv.");
			} else {
				statement.executeUpdate("UPDATE `users` SET `gems` = gems - " + price + " WHERE `id` = " + user.getID());
				//TODO: Rest updaten


				rs.updateInt("gems", gems - price);
				post(user.getName() + ", hier ist dein XPot (x" + amp + ") for " + duration + " min!");
				LOGGER.info("{} -> XPot for {} (x{})", user.getName(), price, amp);
				rs.updateDouble("expRate", amp);
				rs.updateInt("potDuration", duration);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}
}
