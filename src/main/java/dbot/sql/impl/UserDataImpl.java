package dbot.sql.impl;

import dbot.Statics;
import dbot.util.UserCache;
import dbot.util.exception.UserNotFoundException;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static dbot.util.Poster.post;

/**
 * Created by Niklas on 15.03.2017.
 */
public final class UserDataImpl implements UserData {//methode für tick?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.sql.impl.UserDataImpl");
	private static final String UPDATE_QUERY = "UPDATE `users` SET `gems` = ?, `level` = ?, `exp` = ?, `swagLevel` = ?, `swagPoints` = ?, `reminder` = ?, `expRate` = ?, `potDur` = ? WHERE `id` = ?";//lieber upsert?
	private final long id;
	private final String name;
	private final IUser user;
	private int ticks;
	private long lastSeen;
	private int gems;//TODO: getter exceptions?
	private int level;
	private int exp;
	private int swagLevel;
	private int swagPoints;
	private int reminder;
	private int expRate;//1000 == 100%
	private int potDur;

	/*private UserDataImpl(IUser user) {
		this.user = user;
		id = user.getID();
		name = user.getName();
	}*/

	public UserDataImpl(
			IUser user,
			int ticks,
			long lastSeen,
			int gems,
			int level,
			int exp,
			int swagLevel,
			int swagPoints,
			int reminder,
			int expRate,
			int potDur) {//TODO: private? -> Probleme mit UserCache

		this.user = user;
		id = user.getLongID();
		name = user.getName();
		this.ticks = ticks;
		this.lastSeen = lastSeen;
		this.gems = gems;
		this.level = level;
		this.exp = exp;
		this.swagLevel = swagLevel;
		this.swagPoints = swagPoints;
		this.reminder = reminder;
		this.expRate = expRate;
		this.potDur = potDur;
	}

	public static UserDataImpl getUserData(IUser user) {
		return UserCache.getUserData(user);
	}

	public static List<UserDataImpl> getUserData(List<IUser> userList) {
		//List<UserDataImpl> userDataList = new ArrayList<>(userList.size());//gut so? oder wird durch loadfactor die liste direkt vergrößert?
		List<IUser> usersToLoad = new ArrayList<>();
		for (IUser user : userList) {
			if (!UserCache.containsUser(user)) {
				usersToLoad.add(user);
			}
		}
		if (!usersToLoad.isEmpty()) {
			UserCache.loadUsersFromDb(usersToLoad);
		}
		return UserCache.getUserData(usersToLoad);
	}

	public static int update(List<IUser> users) throws IllegalArgumentException {
		if (users.isEmpty()) {
			throw new IllegalArgumentException("userList is empty!");
		}
		//TODO: BATCHUPDATE!!!
		String updateQuery = "UPDTAE `users` SET"
		/*
			1.Fall: user is in cache

			2.Fall: user not in cache
				-> load user

			-> add to batch
			-> execute batch
		 */
		return -1;//return updated users?
	}

	@Override
	public boolean update() {//returns if update was successful//TODO: wirklich hier schon hochladen??? ne
		UserCache.setAccessed(user);
		//"UPDATE `users` SET `gems` = ?, `level` = ?, `exp` = ?, `swagLevel` = ?, `swagPoints` = ?, `reminder` = ?, `expRate` = ?, `potDur` = ?"
		/*try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_QUERY)) {//TODO: lock users?
			ps.setInt(1, gems);
			ps.setInt(2, level);
			ps.setInt(3, exp);
			ps.setInt(4, swagLevel);
			ps.setInt(5, swagPoints);
			ps.setInt(6, reminder);
			ps.setInt(7, expRate);
			ps.setInt(8, potDur);
			ps.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
			return false;
		}*/
		return true;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IUser getUser() {
		return user;
	}

	public void addTick() {//TODO: override + in interface
		ticks++;
	}

	public void setLastSeen(long currTime) {//TODO: ovverride + in interface
		//TODO: logik
	}

	@Override
	public int getGems() {
		return gems;
	}

	@Override
	public void addGems(int gems) {
		this.gems += gems;
	}

	@Override
	public void subGems(int gems) {
		this.gems -= gems;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getExp() {
		return exp;
	}

	@Override
	public void addExp(int addedExp) {
		exp += addedExp;
		while (exp >= getLevelThreshold(level)) {
			exp -= getLevelThreshold(level);
			level++;
			post(":tada: DING! " + name + " is now level " + level + "! :tada:", Statics.tempBotSpam);//TODO: nur auf guilds posten, auf denen der user ist!!!
			LOGGER.info("{} leveled to Level {}", name, level);
		}
	}

	@Override
	public void prestige() {
		if (level < 100) {
			LOGGER.info("{} Level ist nicht hoch genug zum prestigen", name);
			post(name + ", you have to be at least level 100.", Statics.tempBotSpam);//TODO: post
			return;
		}
		int swagPointGain = (int)Math.ceil(Math.sqrt((double)gems / 10000.0) * ((double)swagLevel + 2.0) / ((double)swagPoints + 2.0)) + level - 100;
		swagPoints += swagPointGain;//TODO: bei stats o.Ä. theoretische SP anzeigen + gems zum nächesten
		LOGGER.info("{} gained {} swagPoints by abandoning {} G", name, swagPointGain, gems);
		gems = 0;
		//TODO: ordentliches gem-abziehen, nicer post
		level = 1;
		swagLevel++;
		LOGGER.info("{} now is swagLevel {} with {} swagPoints", name, swagLevel, swagPoints);//TODO: wieviele gems wurden abgezogen?
	}

	@Override
	public int getSwagLevel() {
		return swagLevel;
	}

	@Override
	public int getSwagPoints() {
		return swagPoints;
	}

	@Override
	public int getReminder() {
		return reminder;
	}

	@Override
	public void addReminder(int anzahl) {
		if (reminder < 0) {
			reminder -= anzahl;
		} else {
			reminder += anzahl;
		}
	}

	@Override
	public void negateReminder() {
		reminder = -reminder;
	}

	@Override
	public int getExpRate() {
		return expRate;
	}

	@Override
	public void setExpRate(int expRate) {
		if (expRate < 0) {
			LOGGER.error("{} expRate < 0 ({})", name, expRate);
			throw new IllegalArgumentException("expRate < 0");
		}
		this.expRate = expRate;
	}

	@Override
	public int getPotDur() {
		return potDur;
	}

	@Override
	public void setPotDur(int potDur) {
		if (potDur < 0) {
			LOGGER.error("{} potDur < 0 ({})", name, potDur);
			throw new IllegalArgumentException("potDur < 0");
		}
		this.potDur = potDur;
	}

	@Override
	public void reducePotDur() {
		if (potDur > 0) {
			potDur -= 1;
			if (potDur < 1) {
				setExpRate(1000);
				LOGGER.info("{} XPot empty", name);
				if (reminder > 0) {
					post("Hey, your XPot is empty...", user);//TODO: kauf und staffelung prüfen
					LOGGER.info("{} got reminded", name);
					reminder--;
				}
			}
		}
	}

	@Override
	public String getFormattedExpRate() {
		return new DecimalFormat("#.##").format(((double)expRate) / 1000);
	}

	public static int getLevelThreshold(int level) {//TODO: angucken; auslagern nach util
		//level--;
		if (level < 1) {
			LOGGER.error("level is < 1; getLevelThreshold({})", level);
			throw new IllegalArgumentException("Level darf nicht < 1 sein!");
		} else if (level < 100) {
			return level * 80 + 1000;
		} else {
			return 10000 + 7500 * (int)Math.round(Math.pow(level - 100, 1.5));
		}
	}

	public static void cleanCache() {

	}

	@Override
	public String toString() {
		return name + "<Data>(" + id + ")";
	}

	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!that.getClass().equals(getClass())) return false;
		return this.id == ((UserDataImpl)that).id;
		//return this.id.equals(((UserDataImpl)that).id);//ACHTUNG: ES WIRD NUR ID GEPRÜFT!!
	}

	@Override
	public int hashCode() {
		return (int)id;
	}//FIXME: geht locker besser; so unsicher

}
