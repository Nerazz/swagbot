package dbot;

import static dbot.Poster.post;

import sx.blah.discord.handle.obj.IUser;

public class UserData extends Database {//implements comparable?
	private String id = null;
	private transient IUser user = null;
	private String name = null;
	private int gems = 0;

	private int exp = 0;
	private int level = 1;
	private int swagLevel = 0;
	private double expRate = 1;
	private int potDuration = 0;
	private int swagPoints = 0;
	
	UserData(IUser user) {
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

	void setUser() {
		try {
			user = Statics.GUILD.getUserByID(id);
		} catch(Exception e) {
			System.out.println("User not found: " + name);
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
			post(":tada: DING! " + user + " ist Level " + level + "! :tada:");
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getSwagLevel() {
		return swagLevel;
	}

	public void prestige() {
		if ((level < 100) || (gems < 100000)) {
			System.out.println(name + " ist noch nicht Level 100 oder hat nicht genug Gems!");
			return;
		}
		gems -= 100000;
		level = 1;

		swagPoints += 5;
		swagLevel++;
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
	
	public void setPotDuration(int rpgPotDuration) {
		if (rpgPotDuration < 0) {
			throw new IllegalArgumentException("PotDuration darf nicht < 0 sein!");
		}
		this.potDuration = rpgPotDuration;
	}
	
	void reducePotDuration() {
		if (potDuration > 0) {
			potDuration -= 1;
			if (potDuration < 1) {
				setExpRate(1);
				System.out.println("xpot von " + name + " durch");
			}
		}
	}

	public static int getLevelThreshold(int level) {
		level--;
		if (level < 0) {
			throw new IllegalArgumentException("Level darf nicht < 0 sein!");
		} else if (level < 100) {
			return level * 80 + 1000;
		} else {
			return level * level * 2;
		}
	}

	@Override
	public String toString() {
		return "User: " + name;
	}

	/*@Override
	public boolean equals(Object o) {
		Data data = new Data((IUser)o);
		data = (Data)o;
		if (data.getID() == this.getID()) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}*/
	
}
