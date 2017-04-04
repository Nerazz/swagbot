package dbot.comm;

import dbot.*;

import static dbot.util.Poster.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * class for triggering methods of called commands
 * loads all connected methods to hashmap on load of class
 *
 * @author Niklas Zd
 */
public final class Commands {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Commands");
	/** command map */
	private static final Map<String, Consumer<IMessage>> COMMANDS = new HashMap<>();
	/** command map for admins */
	private static final Map<String, Consumer<IMessage>> ADMIN_COMMANDS = new HashMap<>();

	static {
		/* COMMANDS init */
		COMMANDS.put("commands", Posts::commands);
		COMMANDS.put("info", Posts::info);
		COMMANDS.put("changelog", Posts::changelog);
		COMMANDS.put("shop", Posts::shop);
		COMMANDS.put("prestige", Posts::prestigeInfo);
		COMMANDS.put("plan", Posts::plan);
		//COMMANDS.put("ichwilljetztwirklichresettenundkennedieregelnzuswagpointsundcomindestenseinigermassen", Posts::prestige);TODO: erst prestige fixen
		COMMANDS.put("roll", Roll::main);
		COMMANDS.put("stats", Posts::stats);
		COMMANDS.put("gems", Posts::gems);
		COMMANDS.put("gtop", Posts::globalTop);
		COMMANDS.put("ltop", Posts::localTop);
		COMMANDS.put("buy", Buy::main);
		COMMANDS.put("flip", Flip::main);
		COMMANDS.put("give", Give::main);
		COMMANDS.put("remind", Posts::remind);
		COMMANDS.put("mute", Posts::mute);//FIXME(missingPermissionException)
		//COMMANDS.put("rank", Posts::rank);
		COMMANDS.put("lotto", Lotto::main);//FIXME(timer läuft nicht ordentlich)
		COMMANDS.put("test", Posts::test);

		ADMIN_COMMANDS.put("folo", Admin::forceLogout);
	}

	/**
	 * calls method of triggered command
	 *
	 * @param message for pass through
	 * @param command command that is called
	 */
	public static void trigger(IMessage message, String command) {
		Consumer<IMessage> consumer = COMMANDS.get(command);
		if (consumer == null) {
			LOGGER.info("Command '{}' not found", message.getContent());
			post("Command not found", message.getChannel());
		} else {
			consumer.accept(message);
		}
	}

	/**
	 * calls method of triggered admin command
	 *
	 * @param message for pass through
	 * @param command command that is called
	 */
	public static void adminTrigger(IMessage message, String command) {
		if (!message.getAuthor().getID().equals(Statics.ID_NERAZ)) {
			post(message.getAuthor().getName() + ", you need to be admin for this kinda stuff", message.getChannel());
			return;
		}
		Consumer<IMessage> consumer = ADMIN_COMMANDS.get(command);
		if (consumer == null) {
			post("Command not found...", message.getChannel());
		} else {
			consumer.accept(message);
		}
	}

	/*static List<String> getCommands() {für schlaue command-help liste?
		return new ArrayList<>(COMMANDS.keySet());//Liste aller Keys
	}*/
}