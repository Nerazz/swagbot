package dbot.comm;

import dbot.UserData;
import dbot.DataBase;
import dbot.Poster;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;


public class Commands {
	
	public static void user(Parser parser) {
		Poster pos = new Poster();
		DataBase DB = new DataBase();//object mit allen wichtigen sachen (static) einmal am anfang initialisieren und dann ueberall rein?
		Flip flip = new Flip(pos);
		//DataBase DB = new Info().getDB();//irgendwie so
		//IUser author = super.getAuthor();
		IUser author = parser.getAuthor();
		System.out.println("usertrigger");
		String command = parser.getParams().get(0).toString();//vielleicht zwischenspeichern
		String param1 = null;
		String param2 = null;
		try {
			param1 = parser.getParams().get(1).toString();
			param2 = parser.getParams().get(2).toString();//einfach jedem befehl ganze liste uebergeben
		} catch(Exception e) {
			System.out.println("commands. " + e);
		}
		System.out.println("switch");
		switch (command) {
			case "roll":
				/*Iterator<UserData> iterator = lUDB.iterator();
				while (iterator.hasNext()) {
					uData = iterator.next();
					aUser[i] = uData.getUser();
				}*/
				Roll.m(pos, author, parser.getParams());
				break;
			case "stats":
				UserData dAuthor = DB.getData(author);
				pos.post(author + " ist Level " + dAuthor.getLevel() + " " + dAuthor.getrpgClass() + " mit " + dAuthor.getExp() + "/" + DB.getLevelThreshold(dAuthor.getLevel()) + " Exp.", 60000);
				break;
			case "gems":
				pos.post(author + ", du hast im Moment " + DB.getData(author).getGems() + ":gem:.");
				break;
			case "top":
				DB.getTop(author);
				break;
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
			case "prestige":
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
				flip.m(author, param1, param1, DB.getData(author));
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
			case "buy":
				if ((param1 != null) && (param2 != null)) {
					Buy.two(DB.getData(author), param1, param2);
				}
				else if (param1 != null) {
					Buy.one(DB.getData(author), param1);
				}
				else {
					System.out.println("event.buy fail");
				}
				break;
			default:
				System.out.println("Command nicht gefunden.");
				break;
		}//4z
	}//3z
	
	public static void admin(Parser parser) {
		DataBase DB = new DataBase();
		Poster pos = new Poster();
		IUser author = parser.getAuthor();
		System.out.println("admintrigger");
		//private static final String idNeraz = "97092184821465088";
			if (author.getID().equals("97092184821465088")) {
				String param = parser.getMessage().toString().substring(1);
				
			
				if (param.equals("save")) {
					DB.save();
					pos.post("Aye aye, Meister " + author + " :ok_hand:");
				}
				else if (param.equals("load")) {
					DB.load();
					pos.post("Aye aye, Meister " + author + " :ok_hand:");
				}
			}
			else {
				pos.post(author + ", auf dich Scrub höre ich nicht :joy:");
			}
	}
}



					
			
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
