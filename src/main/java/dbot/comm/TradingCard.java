package dbot.comm;

import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Niklas on 14.03.2017.
 */
final class TradingCard {
	static final Map<String, List<String>> CARD_MAP = new HashMap<>();

	static {
		//load map entries from db
	}

	static void main(IMessage message) {
		//draw cards from booster and add to map
	}
}
