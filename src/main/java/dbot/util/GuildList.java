package dbot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niklas on 18.02.2017.
 */
public final class GuildList {//TODO: lieber DataMap extenden und add overriden für addWithNulls
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.util.GuildList");
	private final DataMap<IGuild, String[]> guildList = new DataMap<>();//TODO: lieber GuildID als Key?; ref = index

	public GuildList() {}

	public void addGuild(int ref, IGuild guild, IChannel botChannel) {
		if (guild == null) {
			LOGGER.error("guild is null!");
			throw new NullPointerException("guild is null");
		} else if (botChannel == null) {
			LOGGER.error("botChannel is null!");
			throw new NullPointerException("botChannel is null");
		} else if (ref < 0) {
			LOGGER.error("ref is < 0: {}", ref);
			throw new IllegalArgumentException("ref is < 0");
		}
		String[] info = {botChannel.getID()};
		guildList.putWithNulls(ref, guild, info);
	}

	public int getRef(IGuild guild) {
		if (guild == null) {
			return -1;
		}
		return guildList.getKeyIndex(guild);
	}

	public IChannel getBotChannel(IGuild guild) {
		if (guild == null) {
			LOGGER.error("guild is null!");
			throw new NullPointerException("guild is null");
		}
		return guild.getChannelByID(guildList.getValueOfKey(guild)[0]);
	}

	public IChannel getBotChannel(int ref) {
		if (ref < 0) {
			LOGGER.error("ref is < 0: {}", ref);
			throw new IllegalArgumentException("ref is < 0");
		}
		IGuild guild = guildList.getKey(ref);//TODO: null abfangen
		if (guild == null) {
			LOGGER.warn("returned channel is null");
			return null;
		}
		return guild.getChannelByID(guildList.getValue(ref)[0]);
	}

	public List<IChannel> getAllBotChannels() {
		List<IChannel> botChannels = new ArrayList<>();
		for (int ref = 0; ref < guildList.size(); ref++) {
			if (guildList.getKey(ref) == null) continue;
			botChannels.add(getBotChannel(ref));
		}
		return botChannels;
	}

	public int size() {
		return guildList.size();
	}

	public IGuild getGuild(int index) {
		if (index < 0) {
			LOGGER.error("index is < 0: {}", index);
			throw new IllegalArgumentException("index is < 0");
		}
		return guildList.getKey(index);
	}

	@Override
	public String toString() {//TODO: besser
		String s = "GuildList: [";
		/*for (int i = 0; i < guildList.size(); i++) {
			s += guildList.toString()
		}*/
		s += guildList.toString();
		s += "]";
		return s;
	}
}
