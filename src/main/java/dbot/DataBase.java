package dbot;

import static java.io.File.separator;

import java.io.*;
import sx.blah.discord.handle.obj.IUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import com.google.gson.*;

public class DataBase {//soll eigentlich static sein?

	private static List<UserData> lUDB;
	private static ServerData SD;//lieber mit getter durchreichen?
	static final int[] rpgLevelThreshold = new int[100];
	//private static final String FILE_PATH = "/home/database.json";
	//private static final String FILE_PATH = "C:\\database.json";
	private static final String FILE_PATH = "database.json";
	//template: "backup_25.08.16-19.40.json"
	private static final String BACKUP_PATH = "backups" + separator + "backup_";
	
	public DataBase() {
		
	}

	static void init() { //TODO: rpgLevelkram nach statics verschieben, init entfernen
		System.out.println("file_path: " + FILE_PATH);
		System.out.println("backup_path: " + BACKUP_PATH);
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

	public static int getLevelThreshold(int level) {
		return rpgLevelThreshold[level - 1];
	}


	public UserScores sortByScore() {//TODO: vielleicht <String, Double> und nur Namen speichern?
		IUser[] users = new IUser[lUDB.size()];
		double[] scores = new double[lUDB.size()];
		int i = 0;
		for (UserData userData : lUDB) {
			users[i] = userData.getUser();
			scores[i] = Math.floor(((userData.getExp() / (double)rpgLevelThreshold[userData.getLevel() - 1]) + userData.getLevel()) * 100) / 100; //*100/100 für Nachkommastellenrundung
			i++;
		}
		for (; i > 1; i--) {//bubblesort
			for (int j = 0; j < (i - 1); j++) {
				if (scores[j] < scores[j + 1]) {
					System.out.println("sort");//TODO: vielleicht tmpvariablen außerhalb?
					IUser tmpUser = users[j];
					double tmpScore = scores[j];
					users[j] = users[j + 1];
					scores[j] = scores[j + 1];
					users[j + 1] = tmpUser;
					scores[j + 1] = tmpScore;
				}
			}
		}
		UserScores userScores = new UserScores(users.length);
		for (i = 0; i < users.length; i++) {
			userScores.add(users[i], scores[i]);
		}
		return userScores;
	}
	
	void load() {//filenotfound, ... exceptions
		System.out.println("loading Databases...");
		try (FileReader fr = new FileReader(FILE_PATH)){
			Gson gson = new Gson();
			DataBaseWrapper dbw = gson.fromJson(fr, DataBaseWrapper.class);
			lUDB = dbw.getUserDataBase();
			SD = dbw.getServerData();
			for (UserData userData : lUDB) {
				userData.setUser();
			}
			System.out.println("loaded " + lUDB.size() + " Users and Serverdata from Database");
		} catch(Exception e) {
			System.out.println("loadError: " + e);
			lUDB = new ArrayList<>();
			SD = new ServerData();
			System.out.println("New Databases for Users and Server created");
		}
	}
	
	public void save(boolean backup) {
		System.out.println("saving Database...");
		String filePath = FILE_PATH;
		if (backup) {
			System.out.println("creating backup...");
			Format format = new SimpleDateFormat("dd.MM.YY-HH.mm");//backup_25.08.16-19.40.json
			filePath = BACKUP_PATH + format.format(new Date()) + ".json";
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();//.serializeNulls?
		try (FileWriter fw = new FileWriter(filePath)){
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

	public UserData getData(IUser user) {//lieber static?
		if (user == null) throw new IllegalArgumentException("User ist NULL in DataBase.findUserData!");
		String id = user.getID();
		for (UserData userData : lUDB) {
			if (userData.getID().equals(id)) return userData;
		}
		return null;
	}

	boolean containsUser(IUser user) {//lieber static?
		return !(getData(user) == null);
	}
	
	@Override
	public String toString() {
		String items = "DB: [";
		for (UserData userdata : lUDB) {
			items += userdata + "; ";
		}
		return items + "]";
	}
}
