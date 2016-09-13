package dbot.comm.items;

import static dbot.Poster.post;

import dbot.UserData;

/**
 * Created by Niklas on 17.08.2016.
 */
public class Xpot {//Buy extenden oder ähnliches?

	public Xpot(UserData userData, String pot) {
		switch(pot) {
			case "tall":
				use(userData, 70, 1.5, 500);
				break;
			case "grande":
				use(userData, 65, 2, 1000);
				break;
			case "venti":
				use(userData, 60, 3, 2000);
				break;
			case "giant":
				use(userData, 120, 5, 9999);
				break;
			default:
				break;
		}
	}

	private void use(UserData userData, int duration, double amp, int price) {
		if (userData.getGems() < price) {//TODO: get price von json, float statt double?
			post(userData.getName() + ", du hast zu wenig :gem:");
		} else if (userData.getExpRate() > 1.0) {
			post(userData.getName() + ", Boost ist noch für " + userData.getPotDuration() + " min aktiv du Noob");
		} else {
			userData.subGems(price);
			post(userData.getName() + ", hier ist dein xpot!");
			System.out.println(userData.getName() + " -> xpot für " + price + "(x" + amp +")");
			userData.setExpRate(amp);
			userData.setPotDuration(duration);
		}
	}
}
