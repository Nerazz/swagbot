package dbot.comm;

import static dbot.Poster.post;

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
		String message = "Umgebene Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + userScores.getUser(i - 1).getName() + " - " + userScores.getScore(i - 1);
		message += "\n" + medalGen(i) + userScores.getUser(i) + " - " + userScores.getScore(i);
		if (i != userScores.getSize()) message += "\n" + medalGen(i + 1) + userScores.getUser(i + 1).getName() + " - " + userScores.getScore(i + 1);
		post(message);
	}
}
