package dbot.comm;

import static dbot.util.Poster.post;
import static dbot.util.Poster.edit;

import dbot.sql.SQLPool;
import dbot.Statics;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.*;

final class Flip {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Flip");
	//private static IMessage roomPost = null;
	private static final String startString = "Offene Flip-Räume:```xl\n";
	private static final int MIN_BET = 500;

	/*static {
		Future<IMessage> fMessage = post(startString + "keine```", -1, channel);//TODO: von db laden
		try {
			roomPost = fMessage.get();
		} catch(InterruptedException|ExecutionException e) {
			LOGGER.error("Error bei init", e);
		}
	}*/

	static void m(IUser author, String params, int ref, IChannel channel) {
		UserData uData = new UserData(author, 1);//gems
		Pattern pattern = Pattern.compile("(\\d+|[a-z]+)(\\s(\\d+|[a-z]+))?");
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) return;
		switch (matcher.group(1)) {
			case "join":
			case "j":
				if (matcher.group(3) == null) {
					post("nicht genug args", channel);
					return;
				}
				join(uData, matcher.group(3), ref, channel);
				break;
			case "close":
			case "c":
				if (matcher.group(3) == null) {
					post("nicht genug args", channel);
					return;
				}
				close(uData, matcher.group(3), channel);
				break;
			case "allin":
			case "ai":
				open(uData, uData.getGems(), matcher.group(3), channel);
				break;
			default:
				pattern = Pattern.compile("(\\d+)");
				Matcher matcher2 = pattern.matcher(matcher.group(1));
				if (!matcher2.matches()) {
					post("nicht genug args", channel);
					return;
				}
				int bet = Integer.parseInt(matcher2.group(1));
				if (uData.getGems() < bet) {
					post(author.getName() + ", du hast zu wenig :gem:", channel);
					return;
				}
				open(uData, bet, matcher.group(3), channel);
				break;
		}

	}

	private static void open(UserData uData, int bet, String side, IChannel channel) {
		side += "";
		side = side.toUpperCase();//TODO: besser machen
		if (bet < MIN_BET) {
			post(uData.getUser() + ", Minimaleinsatz ist " + MIN_BET + ":gem:!", channel);
			return;
		} else if (!(side.equals("TOP") || side.equals("KEK"))) {
			side = (Math.random() < 0.5) ? "TOP" : "KEK";
		}
		uData.subGems(bet);
		uData.update();
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO `flip` (`hostID`, `pot`, `side`) VALUES (?, ?, ?)")) {
			ps.setString(1, uData.getId());
			ps.setInt(2, bet);
			ps.setString(3, side);
			ps.executeUpdate();
			con.commit();
			ResultSet rs = ps.executeQuery("SELECT LAST_INSERT_ID() FROM `flip`");
			rs.next();
			LOGGER.info("{} opened FlipRoom, ID: {}, Pot: {}, Seite: {}", uData.getName(), bet, rs.getInt(1), side);
			post(uData.getName() + " hat neuen Raum um " + bet + ":gem: geöffnet mit ID: " + rs.getInt(1) + " (" + side + ")", channel);
			rs.close();
		} catch(SQLException e) {
			LOGGER.error("SQL failed in open", e);
		}
		System.out.println("open.update");
		updateRoomPost();
	}

	private static void join(UserData clientData, String params, int ref, IChannel channel) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(params);

		if (!matcher.matches()) return;
		int roomID = Integer.parseInt(matcher.group(1));
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `hostID`, `pot`, `side` FROM `flip` WHERE `id` = ?")) {
			ps.setInt(1, roomID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				post("Raum " + roomID + " nicht gefunden.", channel);
				rs.close();
			} else if (clientData.getGems() < rs.getInt("pot")) {
				post(clientData.getUser() + ", du hast zu wenig :gem: um beizutreten.", channel);
				rs.close();
			} else if (clientData.getId().equals(rs.getString("hostID"))) {
				post(clientData.getUser() + ", du kannst nicht mit dir selbst flippen...", channel);
			} else {
				UserData hostData = new UserData(Statics.GUILD_LIST.getGuild(ref).getUserByID(rs.getString("hostID")), 1);//gems;
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
					afterFlip(hostData, clientData, pot, side, channel);
				} else {
					afterFlip(clientData, hostData, pot, side, channel);
				}
				updateRoomPost();
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in join", e);
		}
	}

	private static void afterFlip(UserData winner, UserData looser, int pot, String side, IChannel channel) {//TODO: geht bestimmt besser (weniger params?); CARE BEI WINNER == LOOSER
		winner.addGems(pot * 2);
		winner.update();
		looser.update();//TODO: richtig so? gefühlt wird min. 2x geupdatet pro person
		LOGGER.info("{} won {} Gems vs {}", winner.getName(), pot * 2, looser.getName());
		post(winner.getName() + " hat mit " + side + " gegen " + looser.getName() + " gewonnen und gewinnt " + pot + ":gem:!!", channel);
		post("gz, du hast " + looser.getName() + "s " + pot + ":gem: gewonnen!", winner.getUser());
		post(":cry: du hast deine " + pot + ":gem: gegen " + winner.getName() + " verloren...", looser.getUser());
	}

	private static void close(UserData uData, String param, IChannel channel) {
		Pattern pattern = Pattern.compile("(\\d+)|(all|a)");
		Matcher matcher = pattern.matcher(param);
		if (!matcher.matches()) return;
		if (matcher.group(1) != null) {
			int roomID = Integer.parseInt(param);
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `pot` FROM `flip` WHERE `id` = ? AND `hostID` = ?")) {
				ps.setString(1, param);
				ps.setString(2, uData.getId());
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {
					post("Raum " + roomID + " von " + uData.getName() + " nicht gefunden.", channel);
				} else {
					ps.executeUpdate("DELETE FROM `flip` WHERE id = " + roomID);//TODO: in extra-methode
					con.commit();
					uData.addGems(rs.getInt("pot"));
					uData.update();
					post("Raum " + roomID + " geschlossen.", channel);
				}
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("SQL failed in close/if", e);
			}
		} else {
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `pot` FROM `flip` WHERE `hostID` = ?")) {
				ps.setString(1, uData.getId());
				ResultSet rs = ps.executeQuery();
				int count = 0;
				while (rs.next()) {
					uData.addGems(rs.getInt("pot"));
					count++;
				}
				rs.close();
				ps.executeUpdate("DELETE FROM `flip` WHERE `hostID` = " + uData.getId());
				con.commit();
				uData.update();
				post(count + " Räume geschlossen.", channel);
			} catch(SQLException e) {
				LOGGER.error("SQL failed in close/else", e);
			}
		}
		updateRoomPost();
	}

	/*static IMessage getRoomPost() {
		return roomPost;
	}*/

	private static void updateRoomPost() {
		try {
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `flip`.`id`, `hostID`, `pot`, `side`, `name` FROM `flip` JOIN `users` ON `flip`.`hostID`=`users`.`id`;")) {//TODO: besser machbar?
				ResultSet rs = ps.executeQuery();
				String post = startString;
				while (rs.next()) {
					post += "\nID»'" + rs.getInt("id") + "' Einsatz»'" + rs.getInt("pot") + "' Seite»'" + rs.getString("side") + "' Host»'" + (rs.getString("name")) + "'";
				}
				rs.close();
				post += "\n```";//TODO: keine, wenn leer (!rs.next)
				for (IMessage message : Statics.POST_LIST) {
					edit(message, post);
					System.out.println("update");
				}
			} catch (SQLException e) {
				LOGGER.error("SQL failed in updateRooms", e);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
