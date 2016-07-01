package dbot;

import mib.ml;
import dbot.timer.CDDel;

//import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IChannel;

import sx.blah.discord.api.IDiscordClient;

import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;

public class Poster {
	private static IDiscordClient bClient;
	private static IGuild guild;
	private static IChannel channel;
	
	public Poster() {//abfrage if bClient, guild == null??
	}
	
	public Poster(IDiscordClient bClient, IGuild guild, IChannel channel) {
		this.bClient = bClient;
		this.guild = guild;
		this.channel = channel;
	}
	
	public IMessage post(String message, int duration) {
		IMessage tMessage = bMes(bClient, channel, message, duration);
		return tMessage;
	}
	
	public IMessage post(String message) {
		IMessage tMessage = bMes(bClient, channel, message);
		return tMessage;
	}
	
	public void del(IMessage message) {
		try {
			message.delete();
		} catch(MissingPermissionsException e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(HTTP429Exception e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(DiscordException e) {
			System.out.println("EX: Poster.del: " + e);
		}
	}
	
	//public edit //notwendig?
	
	public static IMessage bMes(IDiscordClient client, IChannel channel, String s) {//exceptions pimpen
		try {
			IMessage message = new MessageBuilder(client).withChannel(channel).withContent(s).build();
			new CDDel(message);
			return message;
		} catch(DiscordException e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(HTTP429Exception e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(MissingPermissionsException e) {
			System.out.println("EX: Poster.del: " + e);
		}
		return null;
	}
	
	public static IMessage bMes(IDiscordClient client, IChannel channel, String s, int duration) {//exceptions pimpen
		try {
			IMessage message = new MessageBuilder(client).withChannel(channel).withContent(s).build();
			if (duration > -1) {
				new CDDel(message, duration);
			}
			return message;
		} catch(DiscordException e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(HTTP429Exception e) {
			System.out.println("EX: Poster.del: " + e);
		} catch(MissingPermissionsException e) {
			System.out.println("EX: Poster.del: " + e);
		}
		return null;
	}
}
