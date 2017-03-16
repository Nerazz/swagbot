package dbot.sql;

import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Niklas on 15.03.2017.
 */
public interface UserData {
	boolean update();
	String getId();
	String getName();
	IUser getUser();
	int getGems();
	void addGems(int gems);
	void subGems(int gems);
	int getLevel();
	int getExp();
	void addExp(int addedExp);//addExp return level oder boolean ob levelup?
	void prestige();
	int getSwagLevel();
	int getSwagPoints();
	int getReminder();
	void addReminder(int anzahl);
	void negateReminder();
	int getExpRate();
	void setExpRate(int expRate);
	int getPotDur();
	void setPotDur(int potDur);
	void reducePotDur();
	String getFormattedExpRate();
	//int getLevelThreshold(int level);//static/default?
}
