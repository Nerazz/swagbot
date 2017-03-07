package dbot.util;

import dbot.Statics;
import dbot.timer.DelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Poster {
	private static final IDiscordClient bClient = Statics.BOT_CLIENT;
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.util.Poster");
	private static final String NUMBER_STRINGS[] = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};

	private Poster() {}
	
	public static Future<IMessage> post(String s, IChannel channel, int duration) {
		return RequestBuffer.request(() -> {
			try {
				IMessage message = new MessageBuilder(bClient).withChannel(channel).withContent(s).build();
				if (duration > 0 && !channel.isPrivate()) {//oder -1?
					new DelTimer(message, duration);
				}
				return message;
			} catch(MissingPermissionsException | DiscordException e) {
				LOGGER.error("failed posting(with duration): {}", s, e);
			}
			return null;
		});
	}

	public static Future<IMessage> post(String s, IChannel channel) { //TODO: läuft so oder futuremessage zwischenspeichern?; könnte besser gemacht werden?
		return post(s, channel, 120000);
	}

	public static Future<IMessage> post(String s, IUser user) {
		return RequestBuffer.request(() -> {
			try {
				IPrivateChannel privateChannel = user.getOrCreatePMChannel();
				return new MessageBuilder(bClient).withChannel(privateChannel).withContent(s).build();
			} catch(MissingPermissionsException | DiscordException e) {
				LOGGER.error("failed posting private(to {}): {}", user.getName(), s, e);
			}
			return null;
		});
	}
	
	public static void del(IMessage message) {
		RequestBuffer.request(() -> {
			try {
				//DelTimer.remove(message);
				message.delete();
			} catch(MissingPermissionsException | DiscordException e) {
				LOGGER.error("failed deleting message", e);
			}
		});
	}
	
	public static void del(IMessage message, int duration) {
		new DelTimer(message, duration);
	}
	
	public static Future<IMessage> edit(IMessage message, String s) {
		return RequestBuffer.request(() -> {
			try {
				message.edit(s);
				return message;
			} catch(MissingPermissionsException | DiscordException e) {
				LOGGER.error("failed editing message", e);
			}
			return null;
		});
	}

	public static String buildNum(int n) {
		Matcher matcher = Pattern.compile("(\\d)(\\d)?(\\d)?").matcher(String.valueOf(n));
		if (!matcher.matches()) {
			LOGGER.error("failed building number: {}", n);
			return "ERROR";
		}
		String s = NUMBER_STRINGS[Integer.parseInt(matcher.group(1))];
		if (matcher.group(2) != null) {
			s += NUMBER_STRINGS[Integer.parseInt(matcher.group(2))];
			if (matcher.group(3) != null) {
				s += NUMBER_STRINGS[Integer.parseInt(matcher.group(3))];
			}
		}
		return s;
	}
	
}
