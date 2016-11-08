package dbot;

import static dbot.Poster.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

public class UserData extends Database {//implements comparable?
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.UserData");
	private String id = null;
	private String name = null;
	private transient IUser user = null;
	private int gems = 0;
	private int level = 1;
	private int exp = 0;
	private double expRate = 1;
	private int potDuration = 0;
	private int swagLevel = 0;
	private int swagPoints = 0;
	private int reminder = 0;

	public UserData(IUser user) {
		this.user = user;
		id = user.getID();
		name = user.getName();
	}
	
	String getID() {
		return id;
	}
	
	public IUser getUser() {
		return user;
	}

	void initUser() {
		try {
			user = Statics.GUILD.getUserByID(id);
		} catch(Exception e) {
			System.out.println("User not found: " + name);//TODO: LOGGER
			System.out.println(e);
		}
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

	void addExp(int rpgExp) {
		this.exp += rpgExp;
		while (this.exp >= getLevelThreshold(level)) {
			this.exp -= getLevelThreshold(level);
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
