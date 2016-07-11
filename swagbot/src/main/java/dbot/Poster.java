package dbot;

import dbot.timer.DelTimer;

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
	//private IMessage message = null;
	
	public Poster() {//abfrage if bClient, guild == null??
	}
	
	public Poster(IDiscordClient bClient, IGuild guild, IChannel channel) {
		this.bClient = bClient;
		this.guild = guild;
		this.channel = channel;
	}
	
	public IMessage post(String s, int duration) {
		try {
			IMessage message = new MessageBuilder(bClient).withChannel(channel).withContent(s).build();
			new DelTimer(message, duration);
			return message;
		} catch(MissingPermissionsException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("MissingEX: Poster.post+dur");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(HTTP429Exception e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("HTTPEX: Poster.post+dur");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(DiscordException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("DiscordEX: Poster.post+dur");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		}
		return null;
	}
	
	public IMessage post(String s) {
		try {
		IMessage message = new MessageBuilder(bClient).withChannel(channel).withContent(s).build();
		new DelTimer(message);
		return message;
		} catch(MissingPermissionsException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("MissingEX: Poster.post");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(HTTP429Exception e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("HTTPEX: Poster.post");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(DiscordException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("DiscordEX: Poster.post");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		}
		return null;
	}
	
	public void del(IMessage message) {
		try {
			message.delete();
		} catch(MissingPermissionsException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("MissingEX: Poster.del");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(HTTP429Exception e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("HTTPEX: Poster.del");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(DiscordException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("DiscordEX: Poster.del");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		}
	}
	
	public void del(IMessage message, int duration) {
		new DelTimer(message, duration);
	}
	
}
