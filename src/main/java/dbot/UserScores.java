package dbot;

import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Niklas on 26.08.2016.
 */
public class UserScores {
	private IUser[] users;
	private double[] scores;
	private int size;
	private int used = 0;

	UserScores(int i) {
		users = new IUser[i];
		scores = new double[i];
		size = i;
	}

	public IUser getUser(int i) {
		return users[i];
	}

	public double getScore(int i) {
		return scores[i];
	}

	public int getSize() {
		return size;
	}

	void add(IUser user, double score) {
		if (used < size) {
			users[used] = user;
			scores[used] = score;
			used++;
		} else {
			System.out.println("UserScores.addPair ERROR");
		}
	}
}
