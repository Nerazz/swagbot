package dbot.comm;

import static dbot.Poster.buildNum;
import static dbot.Poster.post;

import dbot.*;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

/**
 * Created by Niklas on 13.09.2016.
 */
class Posts {
	private static final String medals[] = {":first_place:", ":second_place:", ":third_place:", ":military_medal:"};

	static void stats(IUser user) {
		UserData uData = new UserData(user, 255);//exp, level, expRate, potDur, swagLevel, swagPoints, reminder
		String message = "";
		if (uData.getSwagLevel() > 0) message += " :trident:" + buildNum(uData.getSwagLevel());//TODO: swagpoints anzeigen mit nicem emoji
		message += "\nLevel " + uData.getLevel() + " mit " + uData.getExp() + "/" + UserData.getLevelThreshold(uData.getLevel()) + " Exp";
		//message += "\n" + data.getGems() + ":gem:";

		if (uData.getPotDuration() > 0) {
			message += "\nBoost(x" + uData.getExpRate() + ") ist noch " + uData.getPotDuration() + " min aktiv";
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
		post(user + message);
	}

	private static String medalGen(int i) {
		if (i < 4) {
			return medals[i];
		} else {
			return String.valueOf(i + 1) + ". ";
		}
	}

	static void top() {
		String message = "TOP 5:";
		ArrayList<SQLData> topList = SQLPool.getScoreList();
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserData.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double)UserData.getLevelThreshold(uData.getInt("level"));
			message += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		//System.out.println(message);
		post(message);
	}

	/*static void rank(DataMap<IUser, Double> dataMap, IUser author) {
		int i = 0;
		while ((i < dataMap.size()) && !dataMap.getKey(i).getID().equals(author.getID())) i++;
		String message = "Umgebende R�nge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + dataMap.getKey(i - 1).getName() + " - " + dataMap.getValue(i - 1);
		message += "\n" + medalGen(i) + dataMap.getKey(i) + " - " + dataMap.getValue(i);
		if (i != dataMap.size()) message += "\n" + medalGen(i + 1) + dataMap.getKey(i + 1).getName() + " - " + dataMap.getValue(i + 1);
		post(message);
	}*/

	public static void rankNew() {//TODO: nicht public

	}

	static void info() {
		post(	"v" + Statics.VERSION + "; D4J v" + Statics.DFJ_VERSION + "\n" +
				"» Jede Minute erhalten Leute nach Status:\n" +
				"\t» Online     3 :gem: + Mod durch Swag\n"
		);
	}

	static void changelog() {
		post(	"neuer Shit:\n" +
				"v5.2.x - v5.3.x:\n" +
				"- man kann wieder leveln + Swag wird eingerechnet\n" +
				"- Pots laufen wieder (und wieder kaufbar, aber scheinbar noch in bestimmten Fällen Formatierungsfehler)\n" +
				"- reminder gehen wieder\n" +
				"v5.4.0:\n" +
				"- flip geht wieder (+ derbe gepimpt) :)\n" +
				"- massig Abkürzungen für Befehle am start\n" +
				"- !sourcecode\n" +
				"- besserer logout (noch bisschen buggy lel)" +
				"v5.4.1\n" +
				"- Post vergessen zu editieren" +
				"v5.4.2\n" +
				"- flip Fix Nr.1 (flippen ohne Seite nicht mölich)" +
				"v5.4.3\n" +
				"flip Fix Nr.2 ((:gem:-Verlust duch bestimmte flips))"
		);
	}

	static void shop() {
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
		);
	}

	static void commands() {
		post(	"```xl\n" +
				"1/4 läuft immer noch nicht, rip\n" +
				"!commands               |diese Liste\n" +
				"!changelog              |letzte Anderungen\n" +
				"!info                   |allgemeine Infos zum Swagbot\n" +
				"!shop                   |nicer Laden\n" +
				"!stats                  |Infos des Schreibenden\n" +
				"!gems                   |Eingebers Gems\n" +
				"!buy 'x'                |kauft Item 'x'\n" +
				"!top                    |Rangliste der Top5\n" +
				//"!rank                   |postet umgebende Range des Schreibenden\n" +
				//"!give '@person' 'gems'  |gibt Person Gems\n" +
				"!flip 'gems' ('top/kek')|offnet Coinflip-Raum (statt 'gems' ist auch 'allin' moglich)\n" +
				"!flip join 'ID'         |flippt gegen den Raumersteller\n" +
				"!flip close             |schliesst eigenen Flipraum (Gems werden erstattet)\n" +
				"!flip close all         |siehe !flip close fur alle\n" +
				"!remind                 |togglet Reminder\n" +
				"!prestigeinfo           |Infos zum Prestigen\n" +
				"!roll                   |Roll zwischen 1 und 100\n" +
				"!roll 'x'               |Roll zwischen 1 und 'x'\n" +
				"!roll 'x' 'y'           |Roll zwischen 'x' und 'y'" +
				"```"
		);
	}

	static void prestigeInfo() {
		post(	"Infos zum Prestigen:\n" +
				"- Level wird wieder auf 1 gesetzt und alle :gem: gehen verloren, dafür wird das Swaglevel um 1 erhöht\n" +
				"- jedes Level über 100 gewährt einen Swagpoint\n" +
				"- je mehr :gem: verloren gehen, desto mehr Swagpoints werden erlangt\n" +
				"- je höher das Swaglevel, desto mehr Exp pro Minute\n" +
				"- je mehr Swagpoints, desto mehr :gem: pro Minute\n" +
				"- wenn du sicher bist, dass du prestigen willst, gönn dir mit:\n" +
				"!ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen"
		);
	}
}
