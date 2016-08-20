package dbot.comm.items;

import dbot.Poster;
import dbot.UserData;

/**
 * Created by Niklas on 17.08.2016.
 */
class Xpot {//Buy extenden oder ähnliches?

	Xpot(UserData userData, int duration, int amp, int price) {
		if (userData.getGems() < price) {
			new Poster();
		}
		userData.setExpRate(amp);
		userData.setPotDuration(duration);
	}
}
