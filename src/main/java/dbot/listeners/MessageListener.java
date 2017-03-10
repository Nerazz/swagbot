package dbot.listeners;

import dbot.comm.Commands;
import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Niklas on 23.02.2017.
 */
public final class MessageListener implements IListener<MessageReceivedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listeners.MessageListener");

	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		LOGGER.debug("Message({}): \"{}\"", message.getAuthor().getName(), message.getContent());
		String content = message.getContent().toLowerCase();
		Matcher matcher = Pattern.compile("^!([a-z]+)(\\s.+)?").matcher(content);//TODO: oder mit matcher.find(int start) alle commands in array filtern und als args... übergeben
		if (matcher.matches()) {
			Commands.trigger(message, matcher.group(1));
		} else {
			matcher = Pattern.compile("^§([a-z]+)(\\s.+)?").matcher(content);
			if (matcher.matches()) {
				Commands.adminTrigger(message, matcher.group(1));
			}
		}
		if (!message.getChannel().isPrivate()) {
			Poster.del(message);
		}
	}

}
