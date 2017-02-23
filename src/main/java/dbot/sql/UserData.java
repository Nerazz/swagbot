package dbot.sql;

import static dbot.util.Poster.post;

import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class UserData {//implements comparable?
	private static final String[] VALUES = {"gems", "exp", "level", "expRate", "potDur", "swagLevel", "swagPoints", "reminder"};
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.sql.UserData");
	private final List<String> argsList = new ArrayList<>();
	private String id = null;
	private String name = null;
	private IUser user = null;
	private int ref = -1;
	private int gems = -1;
	private int level = -1;
	private int exp = Integer.MIN_VALUE;
	private int expRate = Integer.MIN_VALUE;//1000 == 100%
	private int potDur = -1;
	private int swagLevel = -1;
	private int swagPoints = -1;
	private int reminder = Integer.MIN_VALUE;

	public UserData(IUser user) {}

	public UserData(IUser user, int load) {
		if (load < 1) {
			LOGGER.error("load < 1 (load: {}) in constructor", load);//TODO: throw Exception
			throw new IllegalArgumentException("load darf nicht < 1 sein!");
		}
		this.user = user;
		id = user.getID();
		name = user.getName();
		String query = "SELECT ";
		int bit = 0;
		boolean last = false;
		/*
		TODO: LOADLOGIK
		 */

	}

	public UserData(IUser user, int ref, int load) {
		if (load < 1 || ref < 0) {
			LOGGER.error("load oder ref < 0 (load: {}, ref: {}) in constructor", load, ref);
			throw new IllegalArgumentException("ref darf nicht < 0 und load < 1 sein!");
		}
		this.user = user;
		id = user.getID();
		name = user.getName();
		this.ref = ref;
		String query = "SELECT ";
		int bit = 0;
		boolean last = false;
		while (load != 0) {
			//if (bit != 0) query += ", ";
			if (last) query += ", ";
			if ((load & 1) == 1) {
				query += "`" + VALUES[bit] + "`";
				argsList.add(VALUES[bit]);
				/*for (int i = 0; i < VALUES[bit].length; i++) {
					if (i != 0) query += ", ";
					query += "`" + VALUES[bit][i] + "`";
					argsList.add(VALUES[bit][i]);

				}*/
				last = true;//geht vielleicht besser
			} else {
				last = false;
			}
			load >>= 1;
			bit += 1;
		}
		query += " FROM `users` WHERE `id` = ?";//TODO: übersichtlicher
		//System.out.println(query);
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {//user not in DB
				LOGGER.info("ADDING USER TO DATABASE: " + name);
				String queryUser = "INSERT INTO `users` (`id`, `name`) VALUES (?, ?)";
				try (PreparedStatement psUser = con.prepareStatement(queryUser)) {//TODO: übersichtlicher machen (+ref)
					psUser.setString(1, user.getID());
					psUser.setString(2, user.getName());
					psUser.executeUpdate();
					con.commit();
				}
				rs = ps.executeQuery();
				rs.next();
			}
			for (String args : argsList) {//TODO: schon ziemlich fail so...
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

			String checkGuild = "SELECT `id` FROM `guild" + ref + "` WHERE `id` = ?";
			try(PreparedStatement psGuild = con.prepareStatement(checkGuild)) {
				//psGuild.setString(1, "guild" + ref);
				psGuild.setString(1, id);
				rs = psGuild.executeQuery();
				if (!rs.next()) {
					String addGuild = "INSERT INTO `guild" + ref + "` (id, name) VALUES (?, (SELECT `name` FROM `users` WHERE `id` = ?))";
					try(PreparedStatement psAddGuild = con.prepareStatement(addGuild)) {
						//psAddGuild.setString(1, "guild" + ref);
						psAddGuild.setString(1, id);
						psAddGuild.setString(2, id);
						psAddGuild.executeUpdate();
						con.commit();
					}
				}
				rs.close();
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in constructor", e);
		}
	}

	/*public static void addUser(IUser user) {
		String id = user.getID();
		String name = user.getName();
		LOGGER.info("ADDING USER TO DATABASE: " + name);
		String queryAddUser = "INSERT INTO `users` (`id`, `name`) VALUES (?, ?)";
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement psUser = con.prepareStatement(queryAddUser)) {//TODO: übersichtlicher machen (+ref)
			psUser.setString(1, user.getID());
			psUser.setString(2, user.getName());
			psUser.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			e.printStackTrace();//TODO: logger
		}
		rs = ps.executeQuery();
		rs.next();
	}*/

	public static void addUsers(List<IUser> userList) {
		addUsers(userList, -1);
	}

	public static void addUsers(List<IUser> userList, int ref) {
		String upsertUser = "INSERT INTO `users` (`id`, `name`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `id` = `id`";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(upsertUser)) {
			for (IUser user : userList) {
				ps.setString(1, user.getID());
				ps.setString(2, user.getName());
				ps.addBatch();
			}
			ps.executeBatch();
			//ps.clearBatch();
			ps.close();
			if (ref >= 0) {
				String upsertGuild = "INSERT INTO `guild" + ref + "` (`id`, `name`) VALUES (?, (SELECT `name` FROM `users` WHERE `id` = ?))";
				try(PreparedStatement psGuild = con.prepareStatement(upsertGuild)) {
					for (IUser user : userList) {
						psGuild.setString(1, user.getID());
						psGuild.setString(2, user.getID());
						psGuild.addBatch();
					}
					ps.executeBatch();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();//TODO: logger
		}
	}

	public void update() {//lieber string für batch-update returnen?
		//"UPDATE `users` SET `gems` = `gems` + " + gems + ", `exp` = " + exp + " WHERE `id` = " + user.getID()
		String update = "UPDATE `users` SET ";//TODO: übersicht
		for (String args : argsList) {
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
			if (argsList.indexOf(args) != argsList.size() - 1) {
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

	public static Object getData(IUser user, int ref, String data) {
		String query = "SELECT `" + data + "` FROM `users` WHERE `id` = ?";//TODO: übersicht
		try (Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			//ps.setString(1, data);
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
			post(":tada: DING! " + name + " ist Level " + level + "! :tada:", Statics.GUILD_LIST.getBotChannel(ref));
			LOGGER.info("{} leveled to Level {}", name, level);
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getSwagLevel() {
		return swagLevel;
	}

	public void prestige() {
		if (level < 100) {
			LOGGER.info("{} Level ist nicht hoch genug zum prestigen", name);
			post(name + ", du musst mindestens Level 100 sein.", Statics.GUILD_LIST.getBotChannel(ref));
			return;
		}
		int swagPointGain = (int)Math.ceil(Math.sqrt((double)gems / 10000.0) * ((double)swagLevel + 2.0) / ((double)swagPoints + 2.0)) + level - 100;
		swagPoints += swagPointGain;//TODO: bei stats o.Ä. theoretische SP anzeigen + gems zum nächesten
		LOGGER.info("{} gained {} swagPoints by abandoning {} G", name, swagPointGain, gems);
		gems = 0;
		//TODO: ordentliches gem-abziehen, nicer post
		level = 1;
		swagLevel++;
		LOGGER.info("{} now is swagLevel {} with {} swagPoints", name, swagLevel, swagPoints);
	}

	public int getSwagPoints() {
		return swagPoints;
	}
	
	public int getExpRate() {
		return expRate;
	}
	
	public void setExpRate(int expRate) {
		this.expRate = expRate;
	}
	
	public int getPotDuration() {
		return potDur;
	}
	
	public void setPotDuration(int potDur) {
		if (potDur < 0) {
			LOGGER.error("{} potDuration ist < 0 in setPotDuration", name);
			throw new IllegalArgumentException("PotDuration darf nicht < 0 sein!");
		}
		this.potDur = potDur;
	}
	
	public void reducePotDuration() {
		if (potDur > 0) {
			potDur -= 1;
			if (potDur < 1) {
				setExpRate(1000);
				LOGGER.info("{} XPot empty", name);
				if (reminder > 0) {
					post("Hey, dein XPot zeigt keine Wirkung mehr...", user);//TODO: kauf und staffelung prüfen
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
			LOGGER.warn("level is < 1; getLevelThreshold({})", level);
			throw new IllegalArgumentException("Level darf nicht < 1 sein!");
		} else if (level < 100) {
			return level * 80 + 1000;
		} else {
			return 10000 + 7500 * (int)Math.round(Math.pow(level - 100, 1.5));
		}
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
