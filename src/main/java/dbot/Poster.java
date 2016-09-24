package dbot;

import dbot.timer.DelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;
import java.util.concurrent.Future;

public class Poster {
	private static final IDiscordClient bClient = Statics.BOT_CLIENT;
	private static final IChannel channel = Statics.GUILD.getChannelByID(Statics.ID_BOTSPAM);
	private static final Logger logger = LoggerFactory.getLogger("dbot.Poster");
	
	private Poster() {}
	
	public static Future<IMessage> post(String s, int duration) {
		return RequestBuffer.request(() -> {
			try {
				IMessage message = new MessageBuilder(bClient).withChannel(channel).withContent(s).build();
				if (duration > 0) {//oder -1?
					new DelTimer(message, duration);
				}
				return message;
			} catch(MissingPermissionsException | DiscordException e) {
				logger.error("failed posting(with duration): {}", s, e);
			}
			return null;
		});
	}

	public static Future<IMessage> post(String s) { //TODO: läuft so oder futuremessage zwischenspeichern?; könnte besser gemacht werden?
		return post(s, 60000);
	}

	public static Future<IMessage> post(String s, IUser user) {
		return RequestBuffer.request(() -> {
			try {
				IPrivateChannel privateChannel = user.getOrCreatePMChannel();
				return new MessageBuilder(bClient).withChannel(privateChannel).withContent(s).build();
			} catch(MissingPermissionsException | DiscordException e) {
				logger.error("failed posting private(to {}): {}", user.getName(), s, e);
			}
			return null;
		});
	}
	
	public static void del(IMessage message) {
		RequestBuffer.request(() -> {
			try {
				message.delete();
			} catch(MissingPermissionsException | DiscordException e) {
				logger.error("failed deleting message", e);
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
				logger.error("failed editing message", e);
			}
			return null;
		});
	}
	
}
