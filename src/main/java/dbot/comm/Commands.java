package dbot.comm;

import dbot.*;

import static dbot.Poster.post;
import static dbot.Poster.del;

import dbot.timer.DelTimer;
import dbot.timer.LottoTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.*;

public class Commands {

	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Commands");

	public static void trigger(IMessage message) {
		IUser author = message.getAuthor();
		LOGGER.debug("Message({}): {}", author.getName(), message.getContent());
		Pattern pattern = Pattern.compile("^!([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(message.getContent().toLowerCase());
		if (matcher.matches()) {
			String params = matcher.group(3);
			
			switch (matcher.group(1)) {
				case "roll":
					Roll.m(author, params);
					break;

				case "stats":
				case "st":
					Posts.stats(author);
					break;

				case "gems":
				case "g":
					//post(author + ", du hast " + dAuthor.getGems() + ":gem:.");
					//SQLPool.getInstance().getData("test", "gems");
					//SQLPool.getData(author.getID(), "gems");
					//post(author + ", du hast " + new UserData(author, 1).getGems() + ":gem:.");
					post(author + ", du hast " + UserData.getData(author, "gems") + ":gem:.");
					break;
				
				case "top":
					Posts.top();
					break;

				case "rank":
					//Posts.rank(database.sortByScore(), author);
					post("coming soon(TM)");
					break;
				
				case "buy":
				case "b":
					Buy.m(author, params);
					break;
				
				case "flip":
				case "f":
					Flip.m(author, params);
					break;

				/*case "raffle":
					RaffleTimer.m(dAuthor, params);
					break;*/

				/*case "lotto":
					Lotto.addTicket(dAuthor, params);
					break;*/

				/*case "give":
					Give.m(dAuthor, params);
					break;*/

				case "remind":
				case "rem":
					UserData uData = new UserData(author, 128);
					uData.negateReminder();
					uData.update();
					post("Reminder getogglet");
					break;

				case "changelog":
				case "cl":
					Posts.changelog();
					break;

				case "prestigeinfo":
				case "pi":
				case "prestige"://TODO: Nachfrage
					Posts.prestigeInfo();
					break;

				/*case "ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen":
					dAuthor.prestige();
					break;*/

				case "info":
					Posts.info();
					break;

				case "shop":
				case "s":
					Posts.shop();
					break;

				case "commands":
				case "cmd":
				case "c":
					Posts.commands();
					break;

				case "sourcecode":
				case "swagcode":
				case "sc":
					if (params == null) {//kp wie sonst, null und "" gehen nicht...
						post("Care, bester Code ever :)):\n" +
								"https://github.com/nerazz/swagbot");
					} else {
						post("https://github.com/nerazz/swagbot/blob/master/src/main/java/dbot/comm/" + params.substring(0,1).toUpperCase() + params.substring(1) + ".java");
					}
					break;

				default:
					LOGGER.info("Command '{}' not found", message.getContent());
					break;
			}
			del(message);//befehl
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
						DelTimer.add(message);
						DelTimer.deleteAll();
						del(Flip.getRoomPost());
						try {
							post("Logging out...:ok_hand:", -1);
							Statics.BOT_CLIENT.logout();
							System.exit(0);
						} catch(DiscordException | RateLimitException e) {
							LOGGER.error("Error while logging out", e);
						}
						break;

					case "forcelo":
						try {
							Statics.BOT_CLIENT.logout();
							System.exit(0);
						} catch (DiscordException | RateLimitException e) {
							LOGGER.error("Error while logging out", e);
						}
						break;

					case "cl":
						DelTimer.checkList();
						break;
					default:
						post("Command nicht erkannt");
						break;
				}
			}
			del(message, 3000);
		} else {//kein Befehl
			del(message, 60000);
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