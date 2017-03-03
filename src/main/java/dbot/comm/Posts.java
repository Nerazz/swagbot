package dbot.comm;

import static dbot.util.Poster.buildNum;
import static dbot.util.Poster.post;

import dbot.*;
import dbot.sql.SQLData;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Niklas on 13.09.2016.
 */
final class Posts {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Posts");
	private static final String medals[] = {":first_place:", ":second_place:", ":third_place:", ":military_medal:"};

	static void stats(IUser user, IChannel channel) {
		UserData uData = new UserData(user, 255);//exp, level, expRate, potDur, swagLevel, swagPoints, reminder
		String message = "";
		if (uData.getSwagLevel() > 0) message += " :trident:" + buildNum(uData.getSwagLevel());//TODO: swagpoints anzeigen mit nicem emoji
		message += "\nLevel " + uData.getLevel() + " mit " + uData.getExp() + "/" + UserData.getLevelThreshold(uData.getLevel()) + " Exp";
		//message += "\n" + data.getGems() + ":gem:";

		if (uData.getPotDuration() > 0) {
			message += "\nBoost(x" + uData.getExpRate() + ") ist noch " + uData.getPotDuration() + " min aktiv";//TODO: Formatierung
		} else {
			message += "\nKein aktiver Boost";
		}
		if (uData.getReminder() != 0) {
			message += "\n" + Math.abs(uData.getReminder()) + " Reminder";
			if (uData.getReminder() > 0) {
				message += "(on)";
			} else {
				message += "(off)";
			}
		}
		post(user + message, channel);
	}

	private static String medalGen(int i) {
		if (i < 4) {
			return medals[i];
		} else {
			return String.valueOf(i + 1) + ". ";
		}
	}

	static void globalTop(IChannel channel) {
		String message = "TOP 5:";
		ArrayList<SQLData> topList = getScoreList();
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserData.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double)UserData.getLevelThreshold(uData.getInt("level"));
			message += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		//System.out.println(message);
		post(message, channel);
	}

	static void localTop(IChannel channel) {
		String message = "TOP 5:";
		int ref = Statics.GUILD_LIST.getRef(channel.getGuild());
		//System.out.println(Statics.GUILD_LIST.toString());
		//System.out.println("ref in localTop: " + ref);
		ArrayList<SQLData> topList = getScoreList(ref);
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserData.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double)UserData.getLevelThreshold(uData.getInt("level"));
			message += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		//System.out.println(message);
		post(message, channel);
	}

	static void rank(IUser author, IChannel channel) {//TODO: global oder local?; bestimmt nice mit sql lösbar
		ArrayList<SQLData> topList = getScoreList();
		String authorId = author.getID();
		int i = 0;
		while ((i < topList.size()) && (!topList.get(i).getString("id").equals(authorId))) i++;
		String message = "Umgebende Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + topList.get(i - 1).getString("name") + " - ?";//TODO:!
		message += "\n" + medalGen(i) + topList.get(i).getString("name") + " - ?";
		if (i != topList.size()) message += "\n" + medalGen(i + 1) + topList.get(i + 1).getString("name") + " - ?";
		post(message, channel);
	}

	/*static void rank(DataMap<IUser, Double> dataMap, IUser author) {
		int i = 0;
		while ((i < dataMap.size()) && !dataMap.getKey(i).getID().equals(author.getID())) i++;
		String message = "Umgebende Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + dataMap.getKey(i - 1).getName() + " - " + dataMap.getValue(i - 1);
		message += "\n" + medalGen(i) + dataMap.getKey(i) + " - " + dataMap.getValue(i);
		if (i != dataMap.size()) message += "\n" + medalGen(i + 1) + dataMap.getKey(i + 1).getName() + " - " + dataMap.getValue(i + 1);
		post(message);
	}*/

	static void info(IChannel channel) {
		post(	"v" + Statics.VERSION + "; D4J v" + Statics.DFJ_VERSION + "\n" +
				"» Jede Minute erhalten Leute nach Status:\n" +
				"\t» Online     3 :gem: + Mod durch Swag\n"
		, channel);
	}

	static void changelog(IChannel channel) {//TODO: umdrehen? (neuster shit oben?); letzte 3 hauptversionen?
		post(	"```neuer Shit:\n" +
				"v5.2.x - v5.3.x:\n" +
				"- man kann wieder leveln + Swag wird eingerechnet\n" +
				"- Pots laufen wieder (und wieder kaufbar, aber scheinbar noch in bestimmten Fällen Formatierungsfehler)\n" +
				"- reminder gehen wieder\n" +
				"v5.4.0:\n" +
				"- flip geht wieder (+ derbe gepimpt) :)\n" +
				"- massig Abkürzungen für Befehle am start\n" +
				"- !sourcecode\n" +
				"- besserer logout (noch bisschen buggy lel)\n" +
				"v5.4.1\n" +
				"- Post vergessen zu editieren\n" +
				"v5.4.2\n" +
				"- flip Fix Nr.1 (flippen ohne Seite nicht mölich)\n" +
				"v5.4.3\n" +
				"- flip Fix Nr.2 ((Gemverlust duch bestimmte flips))\n" +
				"v5.4.4\n" +
				"- xpRate reset nach XPot Fix\n" +
				"v5.5.0\n" +
				"- multi-guild-support\n" +
				"- geupdatet auf D4J-v2.7.0" +
				"v5.5.1\n" +
				"- flip-post-delete-fix\n" +
				"v5.5.2\n" +
				"- whisper bot crash fix\n" +
				"- minütliche Updates stark verbessert\n" +
				"- reminder crash gefixt\n" +
				"```"
		, channel);
	}

	static void shop(IChannel channel) {
		post(
				"der nice Laden hat folgendes im Angebot:\n" +//TODO: nicht weg bei funktion
				"```xl\n" +
				"» XPot\n" +
				" $ » tall     (500G)  - 70  Minuten 1.5x Exp (+50%) $ BEST OFFER $\n" +
				"   » grande   (1000G) - 65  Minuten 2x   Exp (+100%)\n" +
				"   » venti    (2000G) - 60  Minuten 3x   Exp (+200%)\n" +
				"   » giant    (9999G) - 120 Minuten 5x   Exp (+400%)\n" +
				"   » unstable (10000G)- 10  Minuten ??x  Exp (+???%)\n" +
				"» Reminder    (100G)  - Erinnert, wenn XPot nicht mehr wirkt, mit Anzahl moglich\n" +
				"```"
		, channel);
	}

	static void commands(IChannel channel) {
		post(	"```xl\n" +
				"!commands               |diese Liste\n" +
				"!changelog              |letzte Anderungen\n" +
				"!info                   |allgemeine Infos zum Swagbot\n" +
				"!shop                   |nicer Laden\n" +
				"!stats                  |Infos des Schreibenden\n" +
				"!gems                   |Eingebers Gems\n" +
				"!buy 'x'                |kauft Item 'x'\n" +
				"!top                    |Rangliste der lokalen Top5\n" +
				"!gtop                   |Rangliste der globalen Top5\n" +
				//"!rank                   |postet umgebende Range des Schreibenden\n" +
				"!give '@person' 'gems'  |gibt Person Gems\n" +
				"!flip 'gems' ('top/kek')|offnet Coinflip-Raum (statt 'gems' ist auch 'allin' moglich)\n" +
				"!flip join 'ID'         |flippt gegen den Raumersteller\n" +
				"!flip close 'ID'        |schliesst eigenen Flipraum (Gems werden erstattet)\n" +
				"!flip close all         |siehe !flip close fur alle\n" +
				"!remind                 |togglet Reminder\n" +
				"!prestigeinfo           |Infos zum Prestigen\n" +
				"!roll                   |Roll zwischen 1 und 100\n" +
				"!roll 'x'               |Roll zwischen 1 und 'x'\n" +
				"!roll 'x' 'y'           |Roll zwischen 'x' und 'y'" +
				"```"
		, channel);
	}

	static void prestigeInfo(IChannel channel) {
		post(	"Infos zum Prestigen:\n" +
				"- Level wird wieder auf 1 gesetzt und alle :gem: gehen verloren, dafür wird das Swaglevel um 1 erhöht\n" +
				"- jedes Level über 100 gewährt einen Swagpoint\n" +
				"- je mehr :gem: verloren gehen, desto mehr Swagpoints werden erlangt\n" +
				"- je höher das Swaglevel, desto mehr Exp pro Minute\n" +
				"- je mehr Swagpoints, desto mehr :gem: pro Minute\n" +
				"- wenn du sicher bist, dass du prestigen willst, gönn dir mit:\n" +
				"!ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen"
		, channel);
	}

	private static ArrayList<SQLData> getScoreList(int ref) {
		String strings[] = {"name", "level", "exp"};
		String query = "SELECT `users`.`name`, `level`, `exp` FROM `users` JOIN `guild" + ref + "` AS guild ON `users`.`id` = guild.`id` ORDER BY `level` DESC, `exp` DESC";
		ArrayList<SQLData> dataList = new ArrayList<>();
		try(Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					Object data[] = new Object[3];
					data[0] = resultSet.getObject("name");
					data[1] = resultSet.getObject("level");
					data[2] = resultSet.getObject("exp");
					SQLData sqlData = new SQLData(strings, data);
					dataList.add(sqlData);
				}
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in getScoreList", e);
		}
		return dataList;
	}

	private static ArrayList<SQLData> getScoreList() {//TODO: return List<>?; in util oder sql verschieben?
		String strings[] = {"name", "level", "exp"};
		String query = "SELECT `name`, `level`, `exp` FROM `users` ORDER BY `level` DESC, `exp` DESC";
		ArrayList<SQLData> dataList = new ArrayList<>();
		try(Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					Object data[] = new Object[3];
					data[0] = resultSet.getObject("name");
					data[1] = resultSet.getObject("level");
					data[2] = resultSet.getObject("exp");
					SQLData sqlData = new SQLData(strings, data);
					dataList.add(sqlData);
				}
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in getScoreList", e);
		}
		return dataList;
	}
}
