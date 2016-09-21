package dbot.comm;

import static dbot.Poster.post;

import dbot.Statics;
import dbot.UserData;
import dbot.UserScores;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Niklas on 13.09.2016.
 */
class Posts {
	private final static String numbers[] = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};
	private final static String medals[] = {":first_place:", ":second_place:", ":third_place:", ":military_medal:"};

	static void stats(UserData dAuthor) {
		String message = dAuthor.getName();
		if (dAuthor.getSwagLevel() > 0) message += " :trident:" + numberGen(dAuthor.getSwagLevel());
		message += "\nLevel " + dAuthor.getLevel() + " mit " + dAuthor.getExp() + "/" + UserData.getLevelThreshold(dAuthor.getLevel()) + " Exp";
		message += "\n" + dAuthor.getGems() + ":gem:";
		if (dAuthor.getPotDuration() > 0) {
			message += "\nBoost(x" + dAuthor.getExpRate() + ") ist noch " + dAuthor.getPotDuration() + " min aktiv";
		} else {
			message += "\nKein aktiver Boost";
		}

		post(message);
	}

	private static String numberGen(int i) {
		if (i < 10) {
			return numbers[i];
		} else {
			return ">10, rip";//TODO: um >10 kümmern
		}
	}

	private static String medalGen(int i) {
		if (i < 4) {
			return medals[i];
		} else {
			return String.valueOf(i + 1) + ". ";
		}
	}

	static void top(UserScores userScores) {
		String message = "TOP 5:";
		for (int i = 0; (i < userScores.getSize()) && (i < 5); i++) {
			message += "\n" + medalGen(i) + userScores.getUser(i).getName() + " - " + userScores.getScore(i);
		}
		post(message);
	}

	static void rank(UserScores userScores, IUser author) {
		int i = 0;
		while ((i < userScores.getSize()) && !userScores.getUser(i).getID().equals(author.getID())) i++;
		String message = "Umgebende Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + userScores.getUser(i - 1).getName() + " - " + userScores.getScore(i - 1);
		message += "\n" + medalGen(i) + userScores.getUser(i) + " - " + userScores.getScore(i);
		if (i != userScores.getSize()) message += "\n" + medalGen(i + 1) + userScores.getUser(i + 1).getName() + " - " + userScores.getScore(i + 1);
		post(message);
	}

	static void info() {
		post(	"v" + Statics.VERSION + "; D4J v" + Statics.DFJ_VERSION + "\n" +
				"» Jede Minute erhalten Leute nach Status:\n" +
				"\t» Online     3 :gem:\n"
		);
	}

	static void shop() {
		post(
				"der nice Laden hat folgendes im Angebot:\n" +
				"```xl\n" +
				"» xpot\n" +
				" $ » tall    (500G)  - 70  Minuten 1.5x Exp (+50%) $ BEST OFFER $\n" +
				"   » grande  (1000G) - 65  Minuten 2x   Exp (+100%)\n" +
				"   » venti   (2000G) - 60  Minuten 3x   Exp (+200%)\n" +
				"   » giant   (9999G) - 120 Minuten 5x   Exp (+400%)" +
				"```"
		);
	}

	static void commands() {
		post(	"```xl\n" +
				"!commands               |diese Liste\n" +
				"!info                   |allgemeine Infos zum Swagbot\n" +
				"!shop                   |nicer Laden\n" +
				"!stats                  |Infos des Schreibenden\n" +
				"!buy 'x'                |kauft Item 'x'\n" +
				"!top                    |Rangliste der Top5\n" +
				"!rank                   |postet umgebende Range des Schreibenden\n" +
				"!give 'gems' '@person'  |gibt Person Gems\n" +
				"!flip 'gems' ('top/kek')|offnet Coinflip-Raum (statt 'gems' ist auch 'allin' moglich)\n" +
				"!flip join 'ID'         |flippt gegen den Raumersteller\n" +
				"!flip close             |schliesst eigenen Flipraum (Gems werden erstattet)\n" +
				"!prestige               |Swag (ab lvl 100)\n" +
				"!roll                   |Roll zwischen 1 und 100\n" +
				"!roll 'x'               |Roll zwischen 1 und 'x'\n" +
				"!roll 'x' 'y'           |Roll zwischen 'x' und 'y'" +
				"```"
		);


	}
}
