package dbot.util;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Niklas on 18.02.2017.
 */
public class GuildList {//TODO: lieber DataMap extenden und add overriden für addWithNulls
	private final DataMap<IGuild, String[]> guildList = new DataMap<>();//TODO: lieber GuildID als Key?; ref = index
	//private ArrayList<String[]> guildList = new ArrayList<>();

	public GuildList() {}

	public void addGuild(int ref, IGuild guild, IChannel botChannel) {
		if (guild == null || ref < 0 || botChannel == null) {
			System.out.println("ERROR, null!!");//TODO: logger
			return;
		}
		String[] info = {botChannel.getID()};
		guildList.putWithNulls(ref, guild, info);
	}

	public int getRef(IGuild guild) {
		if (guild == null) {
			System.out.println("ERROR, null!!");//TODO: logger
			return -1;
		}
		return guildList.getKeyIndex(guild);
	}

	public IChannel getBotChannel(IGuild guild) {
		if (guild == null) {
			System.out.println("ERROR, null!!");//TODO: logger
			return null;
		}
		return guild.getChannelByID(guildList.getValueOfKey(guild)[0]);
	}

	public IChannel getBotChannel(int ref) {
		if (ref < 0) {
			System.out.println("ERROR, null!!");//TODO: logger
			return null;
		}
		IGuild guild = guildList.getKey(ref);
		return guild.getChannelByID(guildList.getValue(ref)[0]);
	}

	public int size() {
		return guildList.size();
	}

	public IGuild getGuild(int index) {
		if (index < 0) {
			System.out.println("ERROR, null!!");//TODO: logger
			return null;
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
