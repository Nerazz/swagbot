package dbot;

import mib.*;
import dbot.comm.*;
import dbot.timer.*;

import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;

import sx.blah.discord.handle.impl.events.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.AudioStopEvent;

import sx.blah.discord.handle.AudioChannel;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRegion;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Presences;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
//import sx.blah.discord.api.internal;

import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;

import java.util.Optional;
import java.util.List;
import java.util.*;
import java.lang.String.*;
import java.io.*;

public class Events extends Bot {

	/**
	 * Handeln von Events
	 * @author	Niklas Zd
	 */
	 
	private int rnd;
	public RouleTimer rt = null;
	protected static int nBrag = 0;
	private Poster pos;//static?
	private Flip flip;
	
	private static String idGuild;
	private static String idGeneral;
	private static String idLink;
	private static String idSpam;
	private static final String idNeraz = "97092184821465088";//UEBERALL
	protected static String idBot;
	//protected static IUser bot
	
	private static IVoiceChannel vChannel = null;
	private static AudioChannel aChannel;
	private static File file;
	
	private String[] sBrags = new String[] {"RUHE HIER!!elf", "Git off mah lawn", "Ihr kleinen Kinners kriegt gleich ordentlich aufs Maul", "Wer reden kann, muss auch mal die Schnauze halten können!", "HALT STOPP, JETZT REDE ICH", "S T F U B O Y S", "Wengier labern, sonst gibts Vokabeltest!", "Psst ihr Ottos"};
	
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		ml.cpr("~BotReadyEvent~");
		
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		System.out.println("~GuildCreateEvent~");
		if (bInit == false) {
			if (botNR == 0) { //test
				idGuild		= "189459280590667777";//noch zu channels umwandeln?
				idGeneral	= "189459280590667777";
				idSpam		= "189461033096577025";
				idBot		= "189453076111949824";
				
			}
			else if (botNR == 1) { //swag
				idGuild		= "97105817550999552";
				idGeneral	= "97105817550999552";
				idLink		= "104539738651774976";
				idSpam		= "186471214548647936";
				idBot		= "183234453122973697";
			}
			
			
			
			guild = bClient.getGuildByID(idGuild);
			pos = new Poster(bClient, guild, guild.getChannelByID(idSpam));//vorher schon alle channel initialisieren?
			DB = new DataBase(guild);
			DB.load();
			flip = new Flip(pos);
			ml.cpr("Bot joined guild: " + guild.getName());
			
			Timer tTimer = new Timer();
			bInit = true;
		}
		System.out.println("~BOT~READY~");
	}
	
	@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {
		
		ml.cpr("DISCONNECTED!!");
		try {
			DB.save();
			/*bClient.logout();
			Thread.sleep(10000);
			bClient.login();*/
			while(!bClient.isReady()) {
				Thread.sleep(10000);
				ml.cpr("try rec");
				bClient.login();
			}
		} catch(Exception e) {
			System.out.println("--DisconnectEX-- " + e);
		}
		ml.cpr("RECONNECTED!!"); 
		System.exit(1);
	}
	
	/*@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {
		
		ml.cpr("DISCONNECTED!!");
		try {
			DB.save();
			Thread.sleep(3000);
			bClient.login();
			Thread.sleep(5000);
			while(!bClient.isReady()) {
				Thread.sleep(10000);
				ml.cpr("try rec");
				bClient.login();
			}
			ml.cpr("RECONNECTED!!??");
		} catch(Exception e) {
			System.out.println("DISCOEXCEPTION: " + e);//genauer
		}
		
	}*/
	
	@EventSubscriber
	public void onMessageEvent(MessageReceivedEvent event) {//BIS ZUM ERSTEN WHITESPACE FILTERN (!test" "hallo) und als command (content) benutzen
		
		IMessage message = event.getMessage();
		IChannel channel = message.getChannel();
		if (channel == guild.getChannelByID(idSpam)) {//1
			System.out.println("messagetrigger");
			Parser parser = new Parser(message);
		}
		
		//System.out.println(bClient.getGuilds().get(1).getRegion());
		/*try {
			IRegion tRegion = bClient.getRegions().get(1);
			System.out.println(tRegion);
			System.out.println(bClient.getGuilds().get(1));
			bClient.getGuilds().get(1).changeRegion(tRegion);
		} catch(DiscordException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		}*/
		
		
		/*else if (((channel == guild.getChannelByID(idLink)) && (ml.isIn(content, "http://") || ml.isIn(content, "https://"))) && false) {
			System.out.println("linkboys");
			int ID = 23;
			int x = 100;
			int y = 17;
			pos.post("Nicer Post, " + author + " mal sehen, wie gut der ankommt:\n\t\t\tID: "+ ID + "\t" + x + " :+1:\t" + y + ":-1:");
			try {
				Thread.sleep(3000);
				//tmpM.edit("test");
			} catch(Exception e) {
				
			}
		}*/
		
		
		
		else {
			ml.cpr("in falschem Channel");
		}
	}
	
	/*@EventSubscriber
	public void onUserAdded(UserJoinEvent event) {
		event.getUser().addRole("97105817550999552", );
	}*/
	
/*	@EventSubscriber
	public void onUserVoiceChannelJoinEvent(UserVoiceChannelJoinEvent e) {
		if (e.getUser().getID().equals(idBot)) {
			System.out.println("jo");
			if (file != null) {
				try {
					Thread.sleep(2000);
					aChannel = e.getChannel().getAudioChannel();
					aChannel.queueFile(file);
				} catch(Exception ex) {
					System.out.println("voicechannel + " + ex);
				}
			}
			else {
				System.out.println("derber fail");
			}
		}
		else {
			System.out.println("nenene");
		}
	}*/

	
	
	/*private void playFile(AudioChannel vChannel, String path) throws Exception {
		//channel = guild.getVoiceChannelByID("97105817584553984");
		vChannel.join();
		File testFile = new java.io.File("");
		channel.queueFile(testFile);
		
	}*/
	
	
	
}
