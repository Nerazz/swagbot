package dbot.sql;

import static dbot.util.Poster.post;

import dbot.Statics;
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

/**
 * num		load
 * 1		gems
 * 2		exp
 * 4		level
 * 8		expRate
 * 16		potDur
 * 32		swagLevel
 * 64		swagPoints
 * 128		reminder
 *
 */

public final class UserData {//implements comparable?
	private static final String[] VALUES = {"gems", "exp", "level", "expRate", "potDur", "swagLevel", "swagPoints", "reminder"};//TODO: enum?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.sql.UserData");
	private final List<String> loadList = new ArrayList<>();
	private String id = null;
	private String name = null;
	private IUser user = null;
	private int gems = -1;
	private int level = -1;
	private int exp = Integer.MIN_VALUE;
	private int expRate = Integer.MIN_VALUE;//1000 == 100%
	private int potDur = -1;//TODO: potDur -> potDuration in DB, setter, getter?
	private int swagLevel = -1;
	private int swagPoints = -1;
	private int reminder = Integer.MIN_VALUE;

	public UserData() {}

	public UserData(IUser user, int load) {
		this.user = user;
		id = user.getID();
		name = user.getName();
		if(load > 0) {
			fillLoadList(load);
			try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(genSelectQuery())) {
				ps.setString(1, id);
				ResultSet rs = ps.executeQuery();
				rs.next();
				for (String args : loadList) {//TODO: schon ziemlich fail so...
					switch (args) {
						case "gems":
							gems = rs.getInt(args);
							break;
						case "exp":
							exp = rs.getInt(args);
							break;
						case "level":
							level = rs.getInt(args);
							break;
						case "expRate":
							expRate = rs.getInt(args);
							break;
						case "potDur":
							potDur = rs.getInt(args);
							break;
						case "swagLevel":
							swagLevel = rs.getInt(args);
							break;
						case "swagPoints":
							swagPoints = rs.getInt(args);
							break;
						case "reminder":
							reminder = rs.getInt(args);
							break;
						default:
							LOGGER.error("Switch default in constructor from {}", args);
							break;
					}
				}
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("failed loading user: {}", user.getName(), e);
			}
		}
	}

	private void fillLoadList(int load) {
		if (load < 1) {//TODO: kann eigentlich nicht passieren
			LOGGER.error("load < 1 (load: {}) in constructor", load);
			throw new IllegalArgumentException("load darf nicht < 1 sein!");
		}
		int bit = 0;
		while (load != 0) {
			if ((load & 1) == 1) {
				loadList.add(VALUES[bit]);
			}
			load >>= 1;
			bit += 1;
		}
	}

	private String genSelectQuery() {
		String query = "SELECT ";
		for (String arg : loadList) {
			query += "`" + arg + "`,";
		}
		query = query.substring(0, query.length() - 1);
		query += " FROM `users` WHERE `id` = ?";
		return query;
	}

	public static void addUser(IUser user, int ref) {
		if (ref < 0) {
			LOGGER.error("ref < 0 (ref: {}) in constructor", ref);
			throw new IllegalArgumentException("ref darf nicht < 0 sein!");
		}
		String id = user.getID();
		String name = user.getName();
		LOGGER.info("ADDING USER TO DATABASE: " + name);
		String upsertUser = "INSERT INTO `users` (`id`, `name`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `id` = `id`";
		String upsertGuild = "INSERT INTO `guild" + ref + "` (`id`, `name`) VALUES (?, (SELECT `name` FROM `users` WHERE `id` = ?)) ON DUPLICATE KEY UPDATE `id` = `id`";
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement psUser = con.prepareStatement(upsertUser); PreparedStatement psGuild = con.prepareStatement(upsertGuild)) {//TODO: übersichtlicher machen (+ref)
			psUser.setString(1, user.getID());
			psUser.setString(2, user.getName());
			psGuild.setString(1, id);
			psGuild.setString(2, id);
			psUser.executeUpdate();
			con.commit();
			psGuild.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			LOGGER.error("failed adding user: {}", user.getName(), e);
		}
	}

	public static void addUsers(List<IUser> userList, int ref) {
		if (ref < 0) {
			LOGGER.error("ref < 0 (ref: {}) in constructor", ref);
			throw new IllegalArgumentException("ref darf nicht < 0 sein!");
		}
		String upsertUser = "INSERT INTO `users` (`id`, `name`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `id` = `id`";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(upsertUser)) {
			for (IUser user : userList) {
				ps.setString(1, user.getID());
				ps.setString(2, user.getName());
				ps.addBatch();
			}
			ps.executeBatch();//TODO: returned int[] zur info/fehlerbehebung nutzen
			ps.close();
			con.commit();
			String upsertGuild = "INSERT INTO `guild" + ref + "` (`id`, `name`) VALUES (?, (SELECT `name` FROM `users` WHERE `id` = ?)) ON DUPLICATE KEY UPDATE `id` = `id`";
			try(PreparedStatement psGuild = con.prepareStatement(upsertGuild)) {
				for (IUser user : userList) {
					psGuild.setString(1, user.getID());
					psGuild.setString(2, user.getID());
					psGuild.addBatch();
				}
				psGuild.executeBatch();//TODO: returned int[] zur info/fehlerbehebung nutzen
				psGuild.close();
				con.commit();
			}
		} catch(SQLException e) {
			LOGGER.error("failed adding users:", e);
		}
	}

	/*public String genUpdateQuery() {
		String update = "UPDATE `users` SET ";
		for (String args : loadList) {
			switch (args) {//TODO: muss besser gehen
				case "gems":
					update += "`" + args + "` = " + gems;
					break;
				case "exp":
					update += "`" + args + "` = " + exp;
					break;
				case "level":
					update += "`" + args + "` = " + level;
					break;
				case "expRate":
					update += "`" + args + "` = " + expRate;
					break;
				case "potDur":
					update += "`" + args + "` = " + potDur;
					break;
				case "swagLevel":
					update += "`" + args + "` = " + swagLevel;
					break;
				case "swagPoints":
					update += "`" + args + "` = " + swagPoints;
					break;
				case "reminder":
					update += "`" + args + "` = " + reminder;
					break;
				default:
					LOGGER.error("Switch default in update from {}", args);
					break;
			}
		}
		update = update.substring(0, update.length() - 1);//delete last ","
		update += " WHERE `id` = ?";
		return update;
	}*/

	public void update() {//lieber string für batch-update returnen?
		//"UPDATE `users` SET `gems` = `gems` + " + gems + ", `exp` = " + exp + " WHERE `id` = " + user.getID()
		String update = "UPDATE `users` SET ";//TODO: übersicht
		for (String args : loadList) {
			switch (args) {
				case "gems":
					update += "`" + args + "` = " + gems;
					break;
				case "exp":
					update += "`" + args + "` = " + exp;
					break;
				case "level":
					update += "`" + args + "` = " + level;
					break;
				case "expRate":
					update += "`" + args + "` = " + expRate;
					break;
				case "potDur":
					update += "`" + args + "` = " + potDur;
					break;
				case "swagLevel":
					update += "`" + args + "` = " + swagLevel;
					break;
				case "swagPoints":
					update += "`" + args + "` = " + swagPoints;
					break;
				case "reminder":
					update += "`" + args + "` = " + reminder;
					break;
				default:
					LOGGER.error("Switch default in update from {}", args);
					break;
			}
			if (loadList.indexOf(args) != loadList.size() - 1) {
				update += ", ";
			}
		}
		update += " WHERE `id` = ?";
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(update)) {
			ps.setString(1, id);//TODO: batchupdate; ps wiederverwenden
			ps.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			LOGGER.error("SQL failed in update", e);
		}
	}

	public static Object getData(IUser user, String data) {
		String query = "SELECT `" + data + "` FROM `users` WHERE `id` = ?";
		try (Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, user.getID());
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getObject(data);
			}
		} catch (SQLException e) {
			LOGGER.error("SQL failed in getData", e);
		}
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public IUser getUser() {
		return user;
	}
	
	public String getName() {
		return name;
	}
	
	public int getGems() {
		return gems;
	}

	public void addGems(int gems) {
		this.gems += gems;
	}
	
	public void subGems(int gems) {
		this.gems -= gems;
	}
	
	public int getExp() {
		return exp;
	}

	public void addExp(int addedExp) {
		exp += addedExp;
		while (exp >= getLevelThreshold(level)) {
			exp -= getLevelThreshold(level);
			level++;
			List<IChannel> channelList = Statics.GUILD_LIST.getAllBotChannels();
			for (IChannel channel : channelList) {
				post(":tada: DING! " + name + " is now level " + level + "! :tada:", channel);//TODO: nur auf guilds posten, auf denen der user ist!!!
			}
			//post(":tada: DING! " + name + " ist Level " + level + "! :tada:", Statics.GUILD_LIST.getBotChannel(ref));
			LOGGER.info("{} leveled to Level {}", name, level);
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getSwagLevel() {
		return swagLevel;
	}

	public void prestige(int ref) {
		if (level < 100) {
			LOGGER.info("{} Level ist nicht hoch genug zum prestigen", name);
			post(name + ", you have to be at least level 100.", Statics.GUILD_LIST.getBotChannel(ref));//TODO: post
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

	public int getSwagPoints() {
		return swagPoints;
	}
	
	public int getExpRate() {
		return expRate;
	}

	public String getFormattedExpRate() {
		return new DecimalFormat("#.##").format(((double)expRate) / 1000);
	}

	public int getPotDuration() {
		return potDur;
	}
	
	public void reducePotDuration() {
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

	public int getReminder() {
		return reminder;
	}

	public void addReminder(int anzahl) {
		if (reminder < 0) {
			reminder -= anzahl;
		} else {
			reminder += anzahl;
		}
	}

	public void negateReminder() {
		reminder = -reminder;
	}

	public static int getLevelThreshold(int level) {//TODO: angucken
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

	public void setGems(int gems) {
		if (gems < 0) {
			LOGGER.error("{} gems < 0 ({})", name, gems);
			throw new IllegalArgumentException("gems < 0");
		}
		this.gems = gems;
	}

	public void setLevel(int level) {
		if (level < 1) {
			LOGGER.error("{} level < 1 ({})", name, level);
			throw new IllegalArgumentException("level < 1");
		}
		this.level = level;
	}

	public void setExp(int exp) {
		if (exp < 0) {
			LOGGER.error("{} exp < 0 ({})", name, exp);
			throw new IllegalArgumentException("exp < 0");
		}
		this.exp = exp;
	}

	public void setSwagLevel(int swagLevel) {
		if (swagLevel < 0) {
			LOGGER.error("{} swagLevel < 0 ({})", name, swagLevel);
			throw new IllegalArgumentException("swagLevel < 0");
		}
		this.swagLevel = swagLevel;
	}

	public void setSwagPoints(int swagPoints) {
		if (swagPoints < 0) {
			LOGGER.error("{} swagPoints < 0 ({})", name, swagPoints);
			throw new IllegalArgumentException("swagPoints < 0");
		}
		this.swagPoints = swagPoints;
	}

	public void setReminder(int reminder) {
		this.reminder = reminder;
	}

	public void setExpRate(int expRate) {
		if (expRate < 0) {
			LOGGER.error("{} expRate < 0 ({})", name, expRate);
			throw new IllegalArgumentException("expRate < 0");
		}
		this.expRate = expRate;
	}

	public void setPotDur(int potDur) {
		if (potDur < 0) {
			LOGGER.error("{} potDur < 0 ({})", name, potDur);
			throw new IllegalArgumentException("potDur < 0");
		}
		this.potDur = potDur;
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
		return this.id.equals(((UserData)that).id);//ACHTUNG: ES WIRD NUR ID GEPRÜFT!!
	}

	@Override
	public int hashCode() {
		return Integer.parseInt(id);
	}
	
}
