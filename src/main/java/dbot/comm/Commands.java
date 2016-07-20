package dbot.comm;

import dbot.UserData;
import dbot.DataBase;
import dbot.Poster;
import dbot.Statics;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.regex.*;

public class Commands {//noch paar static attribute initialisieren am anfang!!
	private static Poster pos;
	private static DataBase DB;
	private static Flip flip;
	
	public static void trigger(IMessage message) {
		/*Poster pos = new Poster();
		DataBase DB = new DataBase();
		Flip flip = new Flip();*/
		IUser author = message.getAuthor();
		
		Pattern pattern = Pattern.compile("^!([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(message.getContent().toLowerCase());
		/*if (matcher.matches()) {

			System.out.println("'" + matcher.group() + "'");
		}*/
		if (matcher.matches()) {
			UserData dAuthor = DB.getData(author);
			String params = "";//statt params matcher.group(3) übergeben, nullbehandlung nach übergabe machen
			if (matcher.group(3) != null) {
				params = matcher.group(3);
			}
			
			switch (matcher.group(1)) {
				case "roll":
					Roll.m(pos, author, params);
					break;
				
				case "stats":
					pos.post(author + " ist Level " + dAuthor.getLevel() + " " + dAuthor.getrpgClass() + " mit " + dAuthor.getExp() + "/" + DB.getLevelThreshold(dAuthor.getLevel()) + " Exp.");
					break;
				
				case "gems":
					pos.post(author + ", du hast im Moment " + DB.getData(author).getGems() + ":gem:.");
					break;
					
				case "timeleft":
					if (dAuthor.getPotDuration() > 0) {
						pos.post(author + ", dein xpot geht noch " + dAuthor.getPotDuration() + "min.");
					} else {
						pos.post(author + ", du hast keinen aktiven Boost.");
					}
					break;
				
				case "top":
					DB.getTop(author);
					break;
				
				case "buy":
					if (params != "") {
						Buy.m(dAuthor, params);
					}
					break;
				
				case "version":
					pos.post("v" + Statics.VERSION);
					break;
				
				case "flip":
					flip.m(dAuthor, params);
					break;
				
				default:
					System.out.println("ERROR IN COMMANDS");
					break;
			}
			/*System.out.println("*Matches:*");
			for (int i = 1; i < matcher.groupCount() + 1; i++) {
				if (matcher.group(i) != null) {
					System.out.println(matcher.group());
					System.out.println("-----------");
				}
			}*/
		} else if (author.getID().equals(Statics.ID_NERAZ)) {
			pattern = Pattern.compile("^§([a-z]+)(\\s(.+))?");
			matcher = pattern.matcher(message.getContent().toLowerCase());
			
			if (matcher.matches()) {
			
				String params = "";
				if (matcher.group(3) != null) {
					params = matcher.group(3);
				}
			
				switch (matcher.group(1)) {
					case "save":
						DB.save();
						pos.post("Aye aye, Meister " + author + " :ok_hand:", 5000);// TODO:sollte nur kurz dasein (5000 ist gut), siehe futuremessageproblem?
						break;
					default:
						break;
				} /*else {
					pos.post(author + ", auf dich Scrub höre ich nicht :joy:");
				}*/
				
			}
		}
		pos.del(message, 10000);
		
	}
	
	public static void init(Flip tFlip, Poster tPos, DataBase tDB) {
		flip = tFlip;
		pos = tPos;
		DB = tDB;
		System.out.println("TRIGGER INIT");
	}
}
	
	
	
			/*case "give":
				try {
					//System.out.println("give start");
					String test = param2.substring(param2.indexOf('<') + 2, param2.indexOf('>'));
					System.out.println(test);
					IUser ugetter = guild.getUserByID(test);
					if (ugetter.getPresence() == Presences.valueOf("ONLINE")) {
						UserData getter = DB.getData(ugetter);
						Give.m(DB.getData(author), param1, getter);
						pos.post("hat geklappt");
						System.out.println("gave gems");
				}
					else {
						System.out.println("error");
					}
				} catch(Exception e) {
					System.out.println("give error");
				}
				break;*/
			/*case "version":
				pos.post("läuft auf Version " + bVersion);
				break;*/
			/*case "prestige":
				UserData d2Author = DB.getData(author);
				if (d2Author.getLevel() == 100) {
					System.out.println("level gut");
					d2Author.addPresLevel();
					pos.post(author + " ist nun Prestigelvl " + d2Author.getPresLevel());
					d2Author.resetLevel();
				}
				else {
					pos.post("Lowlevelnoobs dürfen das nicht benutzen...");
				}
				break;
			case "flip":
				flip.m(DB.getData(author), parser.getParams());
				break;
			case "join":
				flip.join(author, param1, DB.getData(author));
				break;
			case "test":
				System.out.println(DB.SD.getGems());
			/*case "joinme":
				vChannel = author.getVoiceChannel().get();
				vChannel.join();
				break;
			case "leaveme":
				vChannel = bClient.getOurUser().getVoiceChannel().get();
				if (vChannel != null) {
					vChannel.leave();
				}
				break;
			case "play":
				//IVoiceChannel vChannel = guild.getVoiceChannelByID("189459280590667778");//getUserVoiceChannel
				//aChannel
				vChannel = bClient.getOurUser().getVoiceChannel().get();
				if (vChannel != null) {
					Play.m(vChannel, param[1]);
				}
			/*	vChannel.join();
				try {
					AudioChannel aChannel = vChannel.getAudioChannel();
					//aChannel.join();
					File testFile = new File("high_noon.mp3");
					//File testFile = new File("Maiki Vanics - Vice (Original Mix).mp3");
					//System.out.println(testFile);
					aChannel.queueFile(testFile);
					//aChannel.queueUrl("https://youtu.be/YzmtsvHv1VU");
					aChannel.setVolume(0.5f);
					//aChannel.resume();
				} catch(Exception e) {
					System.out.println("fail");
				}
				
				break;*/
			
		/*	else if ((channel == guild.getChannelByID(idGeneral)) && (nBrag > 0)) {
				nBrag -= 1;
				System.out.println("bragboys");
				ml.bMes(bClient, channel, sBrags[(int)(Math.random() * sBrags.length)]);
			}
			else {
				//ml.cpr("in falschem Channel");
			}
		}//2z
	}//1z
		/*
		else if (ml.isIn(content, "!cs ") && false) {
			String server = content.substring(4);
			IRegion region = guild.getRegion();
			switch (server) {
				case "ams":
				case "amsterdam":
				
					break;
				case "lon":
				case "london":
				
					break;
				case "us":
				case "us east":
					
					break;
				case "frank":
				case "frankfurt":
				
					break;
				default:
					System.out.println("def");
					break;
			}
			
		}
		
		
		/*case "!roulette":
					if (rt == null) {
						rt = new RouleTimer(author);
						rt.RouleChannel = channel;
						tmpM = ml.bMes(bClient, channel, "Neue Runde mit " + author + " aufgemacht!");
						new CDDel(tmpM);
					}
					else if (rt.lRouleUser.isEmpty()) {
						rt = new RouleTimer(author);
						rt.RouleChannel = channel;
						ml.bMes(bClient, channel, "Neue Runde mit " + author + " aufgemacht!");
					}
					else if (!rt.lRouleUser.contains(author)) {
						rt.addRoule(author);
						ml.bMes(bClient, channel, author + " added.");
					}
					else {
						ml.bMes(bClient, channel, author + ", du bist schon eingetragen!");
					}
					break;
				/*case "§logout":
				
					if (author.getID().equals(idNeraz)) {
						while(bClient.isReady()) {
							try {
								bClient.logout();
								Thread.sleep(5000);
							} catch(HTTP429Exception|DiscordException|InterruptedException e) {
								e.printStackTrace();
							} 
						}
						System.exit(0);
					}
					break;
				default:
					break;
			}*/
