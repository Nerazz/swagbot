package dbot.comm;

import static dbot.util.Poster.post;
import static dbot.util.Poster.edit;

import dbot.sql.SQLPool;
import dbot.Statics;
import dbot.sql.UserData;
import dbot.sql.impl.UserDataImpl;
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
	private static final String startString = "Open flip rooms:```xl\n";
	private static final int MIN_BET = 500;

	//static void m(IUser author, String params, int ref, IChannel channel) {
	static void main(IMessage message) {
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		int ref = Statics.GUILD_LIST.getRef(message.getGuild());
		String params = message.getContent().toLowerCase();//TODO: besser machen(siehe roll)
		UserData uData = new UserDataImpl(author, 1);//gems
		params = params.substring(6, params.length());//!flip abschneiden
		Pattern pattern = Pattern.compile("(\\d+|[a-z]+)(\\s(\\d+|[a-z]+))?");//TODO: strikter, wirklich nur erlaubte params
		Matcher matcher = pattern.matcher(params);
		if (!matcher.matches()) {
			LOGGER.info("matcher matcht nicht!");
			return;
		}
		switch (matcher.group(1)) {
			case "join":
			case "j":
				if (matcher.group(3) == null) {
					post("no given roomID", channel);
					return;
				}
				join(uData, matcher.group(3), ref, channel);
				break;
			case "close":
			case "c":
				if (matcher.group(3) == null) {
					post("no given roomID", channel);
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
					post("no given bet", channel);
					return;
				}
				int bet = Integer.parseInt(matcher2.group(1));
				if (uData.getGems() < bet) {
					post(author.getName() + ", you don't have enough :gem:", channel);
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
			post(uData.getUser() + ", minimal bet is " + MIN_BET + ":gem:!", channel);
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
			post(String.format("%s, opened a new room (ID: %d) for %d:gem: (%s)!", uData.getName(), rs.getInt(1), bet, side), channel);
			//post(uData.getName() + " opened a new room with " + bet + ":gem: geöffnet mit ID: " + rs.getInt(1) + " (" + side + ")", channel);
			rs.close();
		} catch(SQLException e) {
			LOGGER.error("SQL failed in open", e);
		}
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
				post("room " + roomID + " not found.", channel);
				rs.close();
			} else if (clientData.getGems() < rs.getInt("pot")) {
				post(clientData.getUser() + ", you don't have enough :gem: to join.", channel);
				rs.close();
			} else if (clientData.getId().equals(rs.getString("hostID"))) {
				post(clientData.getUser() + ", you can't flip with yourself...", channel);
			} else {
				UserData hostData = new UserDataImpl(Statics.GUILD_LIST.getGuild(ref).getUserByID(rs.getString("hostID")), 1);//gems;
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
		//post(winner.getName() + " hat mit " + side + " gegen " + looser.getName() + " gewonnen und gewinnt " + pot + ":gem:!!", channel);
		post(String.format("%s won with %s vs %s and gets %d:gem:!!", winner.getName(), side, looser.getName(), pot), channel);
		post(String.format("Hey, you won %d vs %s", pot, looser.getName()), winner.getUser());
		post(String.format(":cry: you lost your %d:gem: vs %s...", pot, winner.getName()), looser.getUser());
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
					post(String.format("Room %d with creator %s not found", roomID, uData.getName()), channel);
					//post("Raum " + roomID + " von " + uData.getName() + " nicht gefunden.", channel);
				} else {
					ps.executeUpdate("DELETE FROM `flip` WHERE id = " + roomID);//TODO: in extra-methode
					con.commit();
					uData.addGems(rs.getInt("pot"));
					uData.update();
					post(String.format("Closed room %d", roomID), channel);
					//post("Raum " + roomID + " geschlossen.", channel);
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
				post(count + " rooms closed.", channel);
			} catch(SQLException e) {
				LOGGER.error("SQL failed in close/else", e);
			}
		}
		updateRoomPost();
	}

	private static void updateRoomPost() {
		try {
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `flip`.`id`, `hostID`, `pot`, `side`, `name` FROM `flip` JOIN `users` ON `flip`.`hostID`=`users`.`id`;")) {//TODO: besser machbar?
				ResultSet rs = ps.executeQuery();
				String post = startString;
				while (rs.next()) {
					post += "\nID»'" + rs.getInt("id") + "' bet»'" + rs.getInt("pot") + "' side»'" + rs.getString("side") + "' host»'" + (rs.getString("name")) + "'";
				}
				rs.close();
				post += "\n```";//TODO: keine, wenn leer (!rs.next)
				for (IMessage message : Statics.POST_LIST) {
					edit(message, post);
					//System.out.println("update");
				}
			} catch (SQLException e) {
				LOGGER.error("SQL failed in updateRooms", e);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
