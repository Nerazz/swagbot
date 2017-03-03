package dbot.listeners;

import dbot.comm.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Niklas on 23.02.2017.
 */
public final class MessageListener implements IListener<MessageReceivedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.MessageListener");

	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		Commands.trigger(message);
	}

}
