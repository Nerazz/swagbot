package dbot.listener;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.impl.UserDataImpl;
import dbot.util.Poster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Listens to GuildCreateEvents
 *
 * @author Niklas Zd
 * @since 23.02.2017
 */
public final class GuildCreateListener implements IListener<GuildCreateEvent> {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.listener.GuildCreateListener");
	/** lock to wait for ReadyEvent */
	static final Object LOCK = new Object();

	/**
	 * handles the event and looks up guild from database
	 *
	 * @param event the GuildCreateEvent
	 */
	@Override
	public void handle(GuildCreateEvent event) {
		LOGGER.debug("GuildCreateEvent");
		IGuild guild = event.getGuild();
		LOGGER.info("Bot joined {}", guild.getName());
		if (!Statics.BOT_CLIENT.isReady()) {
			synchronized (LOCK) {
				try {
					LOCK.wait();
				} catch (InterruptedException e) {
					LOGGER.error("LOCK interrupted:", e);
				}
			}
		}
		IChannel channel = guild.getChannelsByName("botspam").get(0);//TODO: besser machen
		Statics.tempBotSpam = channel;
		Statics.tempGuild = guild;
		createPost(channel);
	}

	/**
	 * creates flipRoom post in channel
	 *
	 * @param channel channel in which it should get posted
	 */
	public static void createPost(IChannel channel) {
		Future<IMessage> fMessage = Poster.post("Offene Flip-Räume:```xl\nkeine```", channel, -1);
		try {
			Statics.POST_LIST.add(fMessage.get());
		} catch(InterruptedException | ExecutionException e) {
			LOGGER.error("future failed:", e);
		}
	}
}
