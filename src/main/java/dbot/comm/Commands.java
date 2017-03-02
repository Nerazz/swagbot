package dbot.comm;

import dbot.*;

import static dbot.util.Poster.post;
import static dbot.util.Poster.del;

import dbot.sql.UserData;
import dbot.timer.DelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import java.util.regex.*;

public class Commands {

	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Commands");

	public static void trigger(IMessage message) {
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		//System.out.println();
		int ref = Statics.GUILD_LIST.getRef(message.getGuild());
		LOGGER.debug("Message({}): {}", author.getName(), message.getContent());
		Pattern pattern = Pattern.compile("^!([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(message.getContent().toLowerCase());
		if (matcher.matches()) {
			String params = matcher.group(3);
			
			switch (matcher.group(1)) {
				case "roll":
					Roll.m(author, channel, params);
					break;

				case "stats":
				case "st":
					Posts.stats(author, channel);
					break;

				case "gems":
				case "g":
					//post(author + ", du hast " + dAuthor.getGems() + ":gem:.");
					//SQLPool.getInstance().getData("test", "gems");
					//SQLPool.getData(author.getID(), "gems");
					//post(author + ", du hast " + new UserData(author, 1).getGems() + ":gem:.");
					post(author + ", du hast " + UserData.getData(author, "gems") + ":gem:.", channel);
					break;
				
				case "globaltop":
				case "gtop":
					Posts.top(channel);
					break;

				case "top":
					Posts.localTop(channel);
					break;

				case "rank":
					//Posts.rank(database.sortByScore(), author);
					Posts.rank(author, channel);
					//post("coming soon(TM)", channel);
					break;
				
				case "buy":
				case "b":
					Buy.m(author, params, channel);
					break;
				
				case "flip":
				case "f":
					Flip.m(author, params, ref, channel);
					break;

				/*case "raffle":
					RaffleTimer.m(dAuthor, params);
					break;*/

				/*case "lotto":
					Lotto.addTicket(dAuthor, params);
					break;*/

				case "give":
					UserData userData = new UserData(author, 1);//gems
					UserData userDataReceiver = new UserData(message.getMentions().get(0), 1);//gems
					System.out.println(message.getContent());
					Give.m(userData, userDataReceiver, params, channel);
					break;

				case "remind":
				case "rem":
					UserData uData = new UserData(author, 128);
					uData.negateReminder();
					uData.update();
					post("Reminder getogglet", channel);
					break;

				case "changelog":
				case "cl":
					Posts.changelog(channel);
					break;

				case "prestigeinfo":
				case "pi":
				case "prestige"://TODO: Nachfrage
					Posts.prestigeInfo(channel);
					break;

				/*case "ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen":
					dAuthor.prestige();
					break;*/

				case "info":
					Posts.info(channel);
					break;

				case "shop":
				case "s":
					Posts.shop(channel);
					break;

				case "commands":
				case "cmd":
				case "c":
					Posts.commands(channel);
					break;

				/*case "sourcecode":
				case "swagcode":
				case "sc":
					post("Care, bester Code ever :)):\n +" +
							"https://github.com/nerazz/swagbot", channel);

					break;*/

				case "lastditch":
				case "ld":
					post("Meister Niklas letzter Ditch war vor 3 Leben 1669 AD", channel);
					break;

				default:
					LOGGER.info("Command '{}' not found", message.getContent());
					break;
			}
			if (!channel.isPrivate()) del(message);//befehl
		} else if (author.getID().equals(Statics.ID_NERAZ) && message.getContent().startsWith("§")) {//TODO: ohne if-elseif, sondern nur mit ifs und return?
			System.out.println("admin-trigger");
			pattern = Pattern.compile("^§([a-z]+)(\\s(.+))?");
			matcher = pattern.matcher(message.getContent().toLowerCase());
			if (matcher.matches()) {
				//String params = "" + matcher.group(3);
				switch (matcher.group(1)) {
					case "logout":
					case "lo":
						//Flip.closeAll();
						//DelTimer.add(message);
						//DelTimer.deleteAll();
						//del(Flip.getRoomPost());
						try {
							post("Logging out...:ok_hand:", channel, -1);
							Statics.BOT_CLIENT.logout();
							System.exit(0);
						} catch(DiscordException e) {
							LOGGER.error("Error while logging out", e);
						}
						break;

					case "forcelo":
					case "folo":
						try {
							Statics.BOT_CLIENT.logout();
							System.exit(0);
						} catch (DiscordException e) {
							LOGGER.error("Error while logging out", e);
						}
						break;

					case "cl":
						//DelTimer.checkList();
						try {
							post("```\n" + Statics.GUILD_LIST.toString() + "```", channel);
						} catch(Exception e) {
							e.printStackTrace();
						}
						break;

					default:
						post("Command nicht erkannt", channel);
						break;
				}
			}
			if(!channel.isPrivate()) del(message, 3000);
		} else {//kein Befehl
			if(!channel.isPrivate()) del(message, 60000);
		}
	}
}
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
					break;*/