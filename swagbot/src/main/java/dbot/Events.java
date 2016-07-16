package dbot;

import dbot.comm.*;

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

import sx.blah.discord.api.events.EventSubscriber;
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

public class Events {

	/**
	 * Handeln von Events
	 * @author	Niklas Zd
	 */
	 
	private int rnd;
	protected static int nBrag = 0;
	private Poster pos;//static?
	private Flip flip;
	
	private static IVoiceChannel vChannel = null;
	private static AudioChannel aChannel;
	private static File file;
	
	
	protected static DataBase DB;
	protected static IDiscordClient botClient;
	protected static IGuild guild;
	private static boolean bInit = false;
	
	/*public static String idGuild;
	public static String idGeneral;
	public static String idSpam;
	public static String idBot;*/
	
	private String[] sBrags = new String[] {"RUHE HIER!!elf", "Git off mah lawn", "Ihr kleinen Kinners kriegt gleich ordentlich aufs Maul", "Wer reden kann, muss auch mal die Schnauze halten können!", "HALT STOPP, JETZT REDE ICH", "S T F U B O Y S", "Wengier labern, sonst gibts Vokabeltest!", "Psst ihr Ottos"};
	
	protected Events() {
		
	}
	
	protected Events(IDiscordClient botClient) {
		this.botClient = botClient;
	}
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("~BotReadyEvent~");
		
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		System.out.println("~GuildCreateEvent~");
		if (bInit == false) {
			guild = botClient.getGuildByID(Statics.ID_GUILD);
			pos = new Poster(botClient, guild, guild.getChannelByID(Statics.ID_BOTSPAM));//vorher schon alle channel initialisieren?
			DB = new DataBase(guild);
			DB.load();
			flip = new Flip(pos);
			System.out.println("Bot joined guild: " + guild.getName());
			
			new MainTimer();
			bInit = true;
		}
		System.out.println("~BOT~READY~");
	}
	
	
	
	@EventSubscriber
	public void onDiscordDisconnectedEvent(DiscordDisconnectedEvent event) {
		System.out.println("DISCONNECTED!!");
		//DB.save();??
	}
	
	@EventSubscriber
	public void onMessageEvent(MessageReceivedEvent event) {//BIS ZUM ERSTEN WHITESPACE FILTERN (!test" "hallo) und als command (content) benutzen
		
		IMessage message = event.getMessage();
		if (message.getChannel() == guild.getChannelByID(Statics.ID_BOTSPAM)) {//1
			System.out.println("messagetrigger durch '" + message.getContent() + "'");
			Commands.trigger(message);
			//Parser parser = new Parser(message);
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
