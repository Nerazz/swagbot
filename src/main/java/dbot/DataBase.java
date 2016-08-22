package dbot;

import java.io.*;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IGuild;
import java.util.List;
import java.util.*;

import com.google.gson.*;

public class DataBase {//soll eigentlich static sein?

	private static List<UserData> lUDB;
	private static ServerData SD;//lieber mit getter durchreichen?
	private static IGuild guild;
	final static int[] rpgLevelThreshold = new int[100];
	private static final String FILE_PATH = "/home/database.json";
	//private static final String FILE_PATH = "C:\\database.json";
	
	public DataBase() {
		
	}

	static void init(IGuild guild) {
		DataBase.guild = guild;
		for (int i = 0; i < 100; i++) {
			rpgLevelThreshold[i] = i * 80 + 1000;
			//System.out.print("Level " + (i + 1) + " = " + rpgLevelThreshold[i] + "\t|| ");
		}
		System.out.println("DB initialized");
	}
	
	void add(IUser user) {
		if (!containsUser(user)) {
			UserData uData = new UserData(user);
			lUDB.add(uData);
		}
		else {
			System.out.println(user.getName() + " ist in DB schon vorhanden!");
		}
	}
	
	public void add(UserData uData) {
		lUDB.add(uData);
	}
	
	public static int getLevelThreshold(int level) {
		return rpgLevelThreshold[level - 1];
	}
	
	public void getTop(IUser author) {//koennte mit static array schlauer werden
		int i = 0;
		int rangAuthor;
		String s;
		IUser u;
		double d;
		IUser[] aUser = new IUser[lUDB.size()];
		double[] aScore = new double[lUDB.size()];
		UserData uData;
		boolean top = false;
		Iterator<UserData> iterator = lUDB.iterator();
		while (iterator.hasNext()) {
			uData = iterator.next();
			aUser[i] = uData.getUser();
			aScore[i] = Math.floor(((uData.getExp() / (double)rpgLevelThreshold[uData.getLevel() - 1]) + uData.getLevel()) * 100) / 100; //*100/100 für Nachkommastellenrundung
			//System.out.println(aScore[i]);
			i++;
		}

		for (; i > 1; i--) {//bubblesort
			for (int j = 0; j < (i - 1); j++) {
				if (aScore[j] < aScore[j + 1]) {
					u = aUser[j];
					d = aScore[j];
					aUser[j] = aUser[j + 1];
					aScore[j] = aScore[j + 1];
					aUser[j + 1] = u;
					aScore[j + 1] = d;
				}
			}
		}
		s = "TOP 5:\n";
		
		if (lUDB.size() > 5) {
			for (i = 0; i < 5; i++) {
				if (aUser[i].getID().equals(author.getID())) {
					s += (i + 1) + ". " + author + " - " + aScore[i] + "\n";
					top = true;
				} else {
					s += (i + 1) + ". " + aUser[i].getName() + " - " + aScore[i] + "\n";
				}
			}
			
			if (!top) {
				while ((!aUser[i].getID().equals(author.getID())) && (i < lUDB.size())) {
					i++;
				}
				s += (author + ", du bist Rang " + (i + 1) + " mit " + aScore[i] + " Punkten.");
			}
		}
		new Poster().post(s);
		
	}
	
	void load() {//filenotfound, ... exceptions
		System.out.println("loading Databases...");
		try (FileReader fr = new FileReader(FILE_PATH)){
			Gson gson = new Gson();
			DataBaseWrapper dbw = gson.fromJson(fr, DataBaseWrapper.class);
			lUDB = dbw.getUserDataBase();
			SD = dbw.getServerData();
			for (Iterator<UserData> it = lUDB.iterator(); it.hasNext();) {
				it.next().setUser();
			}
			System.out.println("loaded " + lUDB.size() + " Users and Serverdata from Database");
		} catch(Exception e) {
			System.out.println("loadError: " + e);
			lUDB = new ArrayList<UserData>();
			SD = new ServerData();
			System.out.println("New Databases for Users and Server created");
		}
	}
	
	public void save() {
		System.out.println("saving Database...");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();//.serializeNulls?
		try (FileWriter fw = new FileWriter(FILE_PATH)){
			DataBaseWrapper dbw = new DataBaseWrapper();
			dbw.setServerData(SD);
			dbw.setUserDataBase(lUDB);
			gson.toJson(dbw, fw);
			System.out.println("saved Database");
		} catch (IOException e) {
			System.out.println("IO-Exception, save interrupted: " + e);
		}

	}

	ServerData getServerData() {
		return SD;
	}

	boolean containsUser(IUser user) {//lieber static?
		if (getData(user) == null) return false;
		return true;
	}
	
	/*public UserData getData(IUser user) {
		UserData tmpData = findUserData(user);
		if (tmpData == null) {
			System.out.println("NULLPOINTER IN DataBase.getData!");
			return null;
		}

		if ((!this.containsUser(user)) || (user == null)) {//optimieren! bei kontrolle wird gesuchtes objekt bereits gefunden!
			System.out.println("USER FAIL? CODE PRUEFEN!!");
			return null;
		}
		Iterator<UserData> iterator = lUDB.iterator();
		while (iterator.hasNext()) {
			UserData foundData = iterator.next();
			if (foundData.getID().equals(user.getID())) {
				return foundData;
			}
		}
		return null;
	}*/

	public UserData getData(IUser user) {//lieber static?
		if (user == null) throw new IllegalArgumentException("User ist NULL in DataBase.findUserData!");
		String id = user.getID();
		UserData tmpData;
		for (Iterator<UserData> it = lUDB.iterator(); it.hasNext();) {
			tmpData = it.next();
			if (tmpData.getID().equals(id)) return tmpData;
		}
		return null;
	}

	public static IGuild getGuild() {//TODO: besser machen, z.B. von Statics abfragen, wenn es benötigt wird
		return guild;
	}
	
	@Override
	public String toString() {
		String items = "DB: [";
		for (Iterator<UserData> it = lUDB.iterator(); it.hasNext();) {
			items += it.next().toString() + "; ";
		}
		return items + "]";
	}
}
