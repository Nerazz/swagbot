package dbot;

import dbot.timer.DelTimer;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;
import java.util.concurrent.Future;

public class Poster {
	private static final IDiscordClient bClient = Statics.BOT_CLIENT;
	private static final IChannel channel = Statics.GUILD.getChannelByID(Statics.ID_BOTSPAM);
	
	private Poster() {}
	
	public static Future<IMessage> post(String s, int duration) {
		return RequestBuffer.request(() -> {
			try {
				IMessage message = new MessageBuilder(bClient).withChannel(channel).withContent(s).build();
				if (duration > 0) {//oder -1?
					new DelTimer(message, duration);
				}
				return message;
			} catch(MissingPermissionsException e) {
				System.out.println("MissingPermEX: Poster.post+dur");
			} catch(DiscordException e) {
				System.out.println("DiscordEX: Poster.post+dur");
			}
			return null;
		});
	}

	public static Future<IMessage> post(String s) { //TODO: läuft so oder futuremessage zwischenspeichern?; könnte besser gemacht werden?
		return post(s, 60000);
	}

	public static Future<IMessage> post(String s, IPrivateChannel privateChannel) {
		return RequestBuffer.request(() -> {
			try {
				IMessage message = new MessageBuilder(bClient).withChannel(privateChannel).withContent(s).build();
				return message;
			} catch(MissingPermissionsException e) {
				System.out.println("MissingPermEX: Poster.post");
			} catch(DiscordException e) {
				System.out.println("DiscordEX: Poster.post");
			}
			return null;
		});
	}
	
	public static void del(IMessage message) {
		RequestBuffer.request(() -> {
			try {
				message.delete();
			} catch(MissingPermissionsException e) {
				System.out.println("MissingPermEX: Poster.del");
			}  catch(DiscordException e) {
				System.out.println("DiscordEX: Poster.del");
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
			} catch(MissingPermissionsException e) {
				System.out.println("MissingPermEX: Poster.edit");
			} catch(DiscordException e) {
				System.out.println("DiscordEX: Poster.edit");
			}
			return null;
		});
	}
	
}
