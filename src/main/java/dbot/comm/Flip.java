package dbot.comm;

import static dbot.Poster.post;
import static dbot.Poster.edit;

import dbot.SQLPool;
import dbot.Statics;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;

public class Flip {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Flip");
	private static IMessage roomPost = null;
	private static final String startString = "Offene Flip-Räume:```xl\n";
	private static final int MIN_BET = 500;

	static {
		Future<IMessage> fMessage = post(startString + "keine```", -1);//TODO: von db laden
		try {
			roomPost = fMessage.get();
		} catch(InterruptedException|ExecutionException e) {
			LOGGER.error("Error bei init", e);
		}
	}

	static void m(IUser author, String params) {
		UserData uData = new UserData(author, 1);//gems
		Pattern pattern = Pattern.compile("(\\d+|[a-z]+)(\\s(\\d+|[a-z]+))?");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		switch (matcher.group(1)) {
			case "join":
			case "j":
				if (matcher.group(3) == null) {
					post("nicht genug args");
					return;
				}
				join(uData, matcher.group(3));
				break;
			case "close":
			case "c":
				if (matcher.group(3) == null) {
					post("nicht genug args");
					return;
				}
				close(uData, matcher.group(3));
				break;
			case "allin":
			case "ai":
				open(uData, uData.getGems(), matcher.group(3));
				break;
			default:
				pattern = Pattern.compile("(\\d+)");
				Matcher matcher2 = pattern.matcher(matcher.group(1));
				if (!matcher2.matches()) {
					post("nicht genug args");
					return;
				}
				int bet = Integer.parseInt(matcher2.group(1));
				if (uData.getGems() < bet) {
					post(author.getName() + ", du hast zu wenig :gem:");
					return;
				}
				open(uData, bet, matcher.group(3));
				break;
		}

	}

	private static void open(UserData uData, int bet, String side) {
		side += "";
		side = side.toUpperCase();//TODO: besser machen
		if (bet < MIN_BET) {
			post(uData.getUser() + ", Minimaleinsatz ist " + MIN_BET + ":gem:!");
			return;
		} else if (!(side.equals("TOP") || side.equals("KEK"))) {
			side = (Math.random() < 0.5) ? "TOP" : "KEK";
		}
		uData.subGems(bet);
		uData.update();
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO `flip` (`hostID`, `pot`, `side`) VALUES (?, ?, ?)")) {
			ps.setString(1, uData.getID());
			ps.setInt(2, bet);
			ps.setString(3, side);
			ps.executeUpdate();
			con.commit();
			ResultSet rs = ps.executeQuery("SELECT LAST_INSERT_ID() FROM `flip`");
			rs.next();
			LOGGER.info("{} opened FlipRoom, ID: {}, Pot: {}, Seite: {}", uData.getName(), bet, rs.getInt(1), side);
			post(uData.getName() + " hat neuen Raum um " + bet + ":gem: geöffnet mit ID: " + rs.getInt(1) + " (" + side + ")");
			rs.close();
		} catch(SQLException e) {
			LOGGER.error("SQL failed in open", e);
		}
		updateRoomPost();
	}

	private static void join(UserData clientData, String params) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(params);

		if (!matcher.matches()) return;
		int roomID = Integer.parseInt(matcher.group(1));
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `hostID`, `pot`, `side` FROM `flip` WHERE `id` = ?")) {
			ps.setInt(1, roomID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				post("Raum " + roomID + " nicht gefunden.");
				rs.close();
			}else if (clientData.getGems() < rs.getInt("pot")) {
				post(clientData.getUser() + ", du hast zu wenig :gem: um beizutreten.");
				rs.close();
			}else if (clientData.getID().equals(rs.getString("hostID"))) {
				post(clientData.getUser() + ", du kannst nicht mit dir selbst flippen...");
			}else {
				UserData hostData = new UserData(Statics.GUILD.getUserByID(rs.getString("hostID")), 1);//gems
				int pot = rs.getInt("pot");
				String side = rs.getString("side");
				rs.close();
				clientData.subGems(pot);
				ps.executeUpdate("DELETE FROM `flip` WHERE id = " + roomID);
				con.commit();
				updateRoomPost();
				//x hat gewonnen und bekommt ys gems:gem:!
				String flippedSide = (Math.random() < 0.5) ? "TOP" : "KEK";
				if (side.equals(flippedSide)) {
					afterFlip(hostData, clientData, pot, side);
				}else {
					afterFlip(clientData, hostData, pot, side);
				}
				updateRoomPost();
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in join", e);
		}
	}

	private static void afterFlip(UserData winner, UserData looser, int pot, String side) {//TODO: geht bestimmt besser (weniger params?); CARE BEI WINNER == LOOSER
		winner.addGems(pot * 2);
		winner.update();
		looser.update();//TODO: richtig so? gefühlt wird min. 2x geupdatet pro person
		LOGGER.info("{} won {} Gems vs {}", winner.getName(), pot * 2, looser.getName());
		post(winner.getName() + " hat mit " + side + " gegen " + looser.getName() + " gewonnen und gewinnt " + pot + ":gem:!!");
		post("gz, du hast " + looser.getName() + "s " + pot + ":gem: gewonnen!", winner.getUser());
		post(":cry: du hast deine " + pot + ":gem: gegen " + winner.getName() + " verloren...", looser.getUser());
	}

	private static void close(UserData uData, String param) {
		Pattern pattern = Pattern.compile("(\\d+)|(all|a)");
		Matcher matcher = pattern.matcher(param);
		if (!matcher.matches()) return;
		if (matcher.group(1) != null) {
			int roomID = Integer.parseInt(param);
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `pot` FROM `flip` WHERE `id` = ? AND `hostID` = ?")) {
				ps.setString(1, param);
				ps.setString(2, uData.getID());
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {
					post("Raum " + roomID + " von " + uData.getName() + " nicht gefunden.");
				}else {
					ps.executeUpdate("DELETE FROM `flip` WHERE id = " + roomID);//TODO: in extra-methode
					con.commit();
					uData.addGems(rs.getInt("pot"));
					uData.update();
					post("Raum " + roomID + " geschlossen.");
				}
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("SQL failed in close/if", e);
			}
		} else {
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `pot` FROM `flip` WHERE `hostID` = ?")) {
				ps.setString(1, uData.getID());
				ResultSet rs = ps.executeQuery();
				int count = 0;
				while (rs.next()) {
					uData.addGems(rs.getInt("pot"));
					count++;
				}
				rs.close();
				ps.executeUpdate("DELETE FROM `flip` WHERE `hostID` = " + uData.getID());
				con.commit();
				uData.update();
				post(count + " Räume geschlossen.");
			} catch(SQLException e) {
				LOGGER.error("SQL failed in close/else", e);
			}
		}
		updateRoomPost();
	}

	static IMessage getRoomPost() {
		return roomPost;
	}

	private static void updateRoomPost() {
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM `flip`")) {
			ResultSet rs = ps.executeQuery();
			String post = startString;
			while (rs.next()) {
				post += "\nID»'" + rs.getInt("id") + "' Einsatz»'" + rs.getInt("pot") + "' Seite»'" + rs.getString("side") + "' Host»'" + Statics.GUILD.getUserByID(rs.getString("hostID")).getName() + "'";
			}
			rs.close();
			edit(roomPost, post + "```");
		} catch(SQLException e) {
			LOGGER.error("SQL failed in updateRooms", e);
		}
	}
}
