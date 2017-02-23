package dbot.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.DisconnectedEvent;

/**
 * Created by Niklas on 23.02.2017.
 */
public class DisconnectedListener implements IListener<DisconnectedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.DisconnectedListener");

	@Override
	public void handle(DisconnectedEvent event) {
		LOGGER.warn("Bot disconnected due to {}", event.getReason());
	}

}
