package mib;

import dbot.timer.CDDel;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;

import sx.blah.discord.api.IDiscordClient;

public final class ml {
	
	/**
	 * library aus uebersichtlichkeitsgruenden
	 * 
	 * @author Niklas Zdarsky
	 */
	
	
	public static void cpr(String s) {
		System.out.println(s);
	}
	
	public static boolean eq(String s1, String s2) {
		if (s1.equals(s2)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Baut eine Message mit MessageBuilder().
	 * @param channel	Channel, in dem die Message gebuildet werden soll
	 * @param s		Message als String
	 * @return			gebuildete Message
	 */
	
	public static IMessage bMes(IDiscordClient client, IChannel channel, String s) {
		try {
			IMessage message = new MessageBuilder(client).withChannel(channel).withContent(s).build();
			new CDDel(message);
			return message;
		} catch(DiscordException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void delMes(IMessage m) {//booleans fuer success?
		try {
			m.delete();
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(DiscordException e) {
			e.printStackTrace();
		}
	}
	
	public static void delMes(IMessage m, IMessage n) {
		try {
			n.delete();
			m.delete();
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(DiscordException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 
	 * Sagt, ob ein String in einem Anderen enthalten ist.
	 * @param content	String, in dem der Substring sein kann
	 * @param s		zu vergleichender Substring
	 */
	
	public static boolean isIn(String content, String s) {
		if (content.contains(s)) {
			return true;
		}
		return false;
	}
	
}
