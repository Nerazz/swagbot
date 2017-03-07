package dbot.comm;

import dbot.*;

import static dbot.util.Poster.post;
import static dbot.util.Poster.del;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class Commands {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Commands");
	private static final Map<String, Consumer<IMessage>> COMMANDS = new HashMap<>();
	private static final Map<String, Consumer<IMessage>> ADMIN_COMMANDS = new HashMap<>();

	static {
		/* COMMANDS init */
		COMMANDS.put("commands", Posts::commands);
		COMMANDS.put("info", Posts::info);
		COMMANDS.put("changelog", Posts::changelog);
		COMMANDS.put("shop", Posts::shop);
		COMMANDS.put("prestige", Posts::prestigeInfo);
		//COMMANDS.put("ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen", Posts::prestige);TODO: erst prestige fixen
		COMMANDS.put("roll", Roll::m);
		COMMANDS.put("stats", Posts::stats);
		COMMANDS.put("gems", Posts::gems);
		COMMANDS.put("gtop", Posts::globalTop);
		COMMANDS.put("ltop", Posts::localTop);
		COMMANDS.put("buy", Buy::m);//FIXME
		COMMANDS.put("flip", Flip::m);//FIXME
		COMMANDS.put("give", Give::m);
		COMMANDS.put("remind", Posts::remind);
		//COMMANDS.put("rank", Posts::rank);
		//COMMANDS.put("lotto", Lotto::m);

		ADMIN_COMMANDS.put("folo", Admin::forceLogout);
	}

	public static void trigger(IMessage message, String command) {
		Consumer<IMessage> consumer = COMMANDS.get(command);
		if (consumer == null) {
			LOGGER.info("Command '{}' not found", message.getContent());
			post("Command nicht erkannt...", message.getChannel());
		} else {
			consumer.accept(message);
		}
	}

	public static void adminTrigger(IMessage message, String command) {
		if (!message.getAuthor().getID().equals(Statics.ID_NERAZ)) {
			post(message.getAuthor().getName() + ", du bist kein Admin :confused:", message.getChannel());
			return;
		}
		Consumer<IMessage> consumer = ADMIN_COMMANDS.get(command);
		if (consumer == null) {
			post("Command nicht erkannt...", message.getChannel());
		} else {
			consumer.accept(message);
		}
	}

	static List<String> getCommands() {
		return new ArrayList<>(COMMANDS.keySet());//Liste aller Keys
	}
}

	/*
		int ref = Statics.GUILD_LIST.getRef(message.getGuild());
		LOGGER.debug("Message({}): {}", author.getName(), message.getContent());
		Pattern pattern = Pattern.compile("^!([a-z]+)(\\s(.+))?");
		Matcher matcher = pattern.matcher(message.getContent().toLowerCase());
		if (matcher.matches()) {
			String params = matcher.group(3);
			
			switch (matcher.group(1)) {


				case "rank":
					//Posts.rank(database.sortByScore(), author);
					//Posts.rank(author, channel);
					post("coming soon(TM)", channel);
					break;
				


				/*case "raffle":
					RaffleTimer.m(dAuthor, params);
					break;*/

				/*case "lotto":
					Lotto.addTicket(dAuthor, params);
					break;*/


				/*case "ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen":
					dAuthor.prestige();
					break;*/
	/*
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
						break;*/