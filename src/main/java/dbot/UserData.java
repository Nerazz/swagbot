package dbot;

import static dbot.Poster.post;

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
 * +num		+load
 * 1		gems
 * 2		exp + level
 * 4		expRate + potDur
 * 8		swagLevel + swagPoints
 * 16		reminder
 */

public class UserData extends Database {//implements comparable?
	//private static final String[] VALUES = {"'gems'", "'exp', 'level'", "'expRate', 'potDuration'", "'swagLevel', 'swagPoints'", "'reminder'"};
	private static final String[][] VALUES = {{"gems"}, {"exp", "level"}, {"expRate", "potDuration"}, {"swagLevel", "swagPoints"}, {"reminder"}};
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.UserData");
	private final List<String> argsList = new ArrayList<>();
	//private final String[] disc;
	//private final Object[] data;
	private String id = null;
	private String name = null;
	private IUser user = null;
	private int gems = -1;
	private int level = -1;
	private int exp = -1;
	private double expRate = -1;
	private int potDuration = -1;
	private int swagLevel = -1;
	private int swagPoints = -1;
	private int reminder = Integer.MIN_VALUE;

	public UserData(IUser user) {}

	public UserData(IUser user, int load) {
		if (load < 1) {
			System.out.println("ERROR in UserData, load < 0");
			return;
		}
		this.user = user;
		id = user.getID();
		name = user.getName();
		String query = "SELECT ";
		int bit = 0;
		boolean last = false;
		while (load != 0) {
			//if (bit != 0) query += ", ";
			if (last) query += ", ";
			if ((load & 1) == 1) {
				for (int i = 0; i < VALUES[bit].length; i++) {
					if (i != 0) query += ", ";
					query += "`" + VALUES[bit][i] + "`";
					argsList.add(VALUES[bit][i]);
					last = true;//geht vielleicht besser
				}

			} else {
				last = false;
			}
			load >>= 1;
			bit += 1;
		}
		query += " FROM `users` WHERE `id` = " + id;
		//System.out.println(query);
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {//user not in DB
				LOGGER.info("ADDING USER TO DATABASE: " + name);
				try (PreparedStatement psAdd = con.prepareStatement("INSERT INTO `users` (`id`, `name`) VALUES (?, ?)")) {
					psAdd.setString(1, user.getID());
					psAdd.setString(2, user.getName());
					psAdd.executeUpdate();
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
						expRate = rs.getDouble(args);
						break;
					case "potDuration":
						potDuration = rs.getInt(args);
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
						System.out.println("ERROR in UserData switch!");
						break;
				}
			}
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}

	public void update() {//lieber string für batch-update returnen?
		//"UPDATE `users` SET `gems` = `gems` + " + gems + ", `exp` = " + exp + " WHERE `id` = " + user.getID()
		String update = "UPDATE `users` SET ";
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
				case "potDuration":
					update += "`" + args + "` = " + potDuration;
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
					System.out.println("ERROR in UserData UPDATE switch!");
					break;
			}
			if (argsList.indexOf(args) != argsList.size() - 1) {
				update += ", ";
			}
		}
		update += " WHERE `id` = " + id;
		//System.out.println(update);
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(update)) {
			ps.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return null;
	}
	
	String getID() {
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

	void addExp(int addedExp) {
		exp += addedExp;
		while (exp >= getLevelThreshold(level)) {
			exp -= getLevelThreshold(level);
			level++;
			post(":tada: DING! " + name + " ist Level " + level + "! :tada:");
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
			post(name + ", du musst mindestens Level 100 sein.");
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

	int getSwagPoints() {
		return swagPoints;
	}
	
	public double getExpRate() {
		return expRate;
	}
	
	public void setExpRate(double expRate) {
		this.expRate = expRate;
	}
	
	public int getPotDuration() {
		return potDuration;
	}
	
	public void setPotDuration(int potDuration) {
		if (potDuration < 0) {
			LOGGER.error("{} potDuration ist < 0", name);
			throw new IllegalArgumentException("PotDuration darf nicht < 0 sein!");
		}
		this.potDuration = potDuration;
	}
	
	void reducePotDuration() {
		if (potDuration > 0) {
			potDuration -= 1;
			if (potDuration < 1) {
				setExpRate(1);
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

	public static int getLevelThreshold(int level) {
		level--;
		if (level < 0) {
			LOGGER.warn("level is < 0");
			throw new IllegalArgumentException("Level darf nicht < 0 sein!");
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
