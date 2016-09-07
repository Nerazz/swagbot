package dbot.comm;

import static dbot.Poster.post;

import dbot.UserScores;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Niklas on 26.08.2016.
 */
final class Ranking {

	private Ranking() {}

	static void topTen(UserScores userScores, IUser author) {
		String message = "TOP 5:\n";
		boolean top = false;
		for (int i = 0; i < userScores.getSize(); i++) {
			for (; (i < 5 ) && (i < userScores.getSize()); i++) {
				if (userScores.getUser(i).getID().equals(author.getID())) {
					message += (i + 1) + ". " + author + " - " + userScores.getScore(i) + "\n";
					top = true;
				} else {
					message += (i + 1) + ". " + userScores.getUser(i).getName() + " - " + userScores.getScore(i) + "\n";
				}
			}
			if (!top) {
				if (userScores.getUser(i).getID().equals(author.getID())) {
					message += author + ", du bist Rang " + (i + 1) + " mit " + userScores.getScore(i) + "Punkten.";//TODO: kaputt?
					break;
				}
			}
			post(message);
		}
	}

	static void rank(UserScores userScores, IUser author) {//TODO: arrayindexoutofbounds
		int i = 0;
		while ((i < userScores.getSize()) && !userScores.getUser(i).getID().equals(author.getID())) i++;
		String message = "Umgebene Ranks:\n";
		if (i != 0) message += (i + 1) + ". " + userScores.getUser(i - 1).getName() + " - " + userScores.getScore(i - 1) + "\n";
		message += (i + 1) + ". " + userScores.getUser(i) + " - " + userScores.getScore(i);
		if (i != userScores.getSize()) message += "\n" + (i + 1) + ". " + userScores.getUser(i + 1).getName() + " - " + userScores.getScore(i + 1);
		post(message);
	}
}
