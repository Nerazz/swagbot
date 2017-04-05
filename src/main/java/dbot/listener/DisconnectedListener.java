package dbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;

/**
 * Listens to DisconnectedEvents
 *
 * @author Niklas Zd
 * @since 23.02.2017
 */
public final class DisconnectedListener implements IListener<DisconnectedEvent> {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listener.DisconnectedListener");

	/**
	 * handles the event
	 *
	 * @param event the DisconnectedEvent
	 */
	@Override
	public void handle(DisconnectedEvent event) {
		LOGGER.warn("Bot disconnected due to {}", event.getReason());
	}

}
