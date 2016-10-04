package dbot.comm;

import dbot.Database;
import dbot.UserData;

import static dbot.Poster.post;
import static dbot.Poster.del;
import dbot.Statics;

import dbot.timer.LottoTimer;
import dbot.timer.RaffleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.regex.*;

public class Commands {

	private static final Database database = Database.getInstance();
	private static final Logger logger = LoggerFactory.getLogger("dbot.comm.Commands");

	public static void trigger(IMessage message) {
		IUser author = message.getAuthor();
		logger.debug("Message({}): {}", author.getName(), message.getContent());
		Pattern pattern = Pattern.compile("^!([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(message.getContent().toLowerCase());
		if (matcher.matches()) {
			UserData dAuthor = database.getData(author);
			String params = "" + matcher.group(3);
			
			switch (matcher.group(1)) {
				case "roll":
					Roll.m(author, params);
					break;

				case "stats":
					Posts.stats(dAuthor);
					break;

				case "gems":
					post(author.getName() + ", du hast " + dAuthor.getGems() + ":gem:.");
					break;
				
				case "top":
					Posts.top(database.sortByScore());
					break;

				case "rank":
					Posts.rank(database.sortByScore(), author);
					break;
				
				case "buy":
					Buy.m(dAuthor, params);
					break;
				
				case "flip":
					Flip.m(dAuthor, params);
					break;

				case "raffle":
					RaffleTimer.m(dAuthor, params);
					break;

				case "lotto":
					new LottoTimer(dAuthor, params);
					break;

				case "give":
					Give.m(dAuthor, params);
					break;

				case "remind":
					dAuthor.negateReminder();
					post("Reminder getogglet");
					break;

				case "changelog":
					Posts.changelog();
					break;

				case "prestigeinfo":
				case "prestige"://TODO: Nachfrage
					Posts.prestigeInfo();
					break;

				case "ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen":
					dAuthor.prestige();
					break;

				case "info":
					Posts.info();
					break;

				case "shop":
					Posts.shop();
					break;

				case "commands":
					Posts.commands();
					break;

				default:
					logger.info("Command '{}' not found", message.getContent());
					break;
			}
			del(message, 3000);
		} else if (author.getID().equals(Statics.ID_NERAZ)) {
			pattern = Pattern.compile("^§([a-z]+)(\\s(.+))?");
			matcher = pattern.matcher(message.getContent().toLowerCase());
			if (matcher.matches()) {
				//String params = "" + matcher.group(3);
				switch (matcher.group(1)) {
					case "save":
						Database.getInstance().save(false);
						post("Aye aye, Meister " + author.getName() + " :ok_hand:", 5000);
						break;
					case "logout":
						Flip.closeAll();
						database.save(false);
						del(message);//TODO: pool an messages, die es noch gibt, die dann gelöscht werden?
						del(Flip.getRoomPost());
						try {
							Statics.BOT_CLIENT.logout();
							System.exit(0);
						} catch(DiscordException | RateLimitException e) {
							logger.error("Error while logging out", e);
						}
						break;
					default:
						break;
				}
			}
			del(message, 30000);
		} else {//kein Befehl
			del(message, 30000);
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