package dbot;

import static java.io.File.separator;

import java.io.*;

import dbot.comm.FlipRoom;
import sx.blah.discord.handle.obj.IUser;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import com.google.gson.*;

public class Database {

	private static Database instance = null;
	private static ServerData serverData;
	private static List<UserData> userDataList;
	//private static final String FILE_PATH = "/home/database.json";
	//private static final String FILE_PATH = "C:\\database.json";
	private static final String FILE_PATH = "database.json";
	//template: "backup_25.08.16-19.40.json"
	private static final String BACKUP_PATH = "backups" + separator + "backup_";
	
	Database() {}

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	void add(IUser user) {
		if (!containsUser(user)) {
			UserData uData = new UserData(user);
			userDataList.add(uData);
		}
		else {
			System.out.println(user.getName() + " ist in DB schon vorhanden!");
		}
	}

	public UserScores sortByScore() {//TODO: vielleicht <String, Double> und nur Namen speichern?
		IUser[] users = new IUser[userDataList.size()];
		double[] scores = new double[userDataList.size()];
		int i = 0;
		for (UserData userData : userDataList) {
			users[i] = userData.getUser();
			scores[i] = Math.floor(((userData.getExp() / (double)UserData.getLevelThreshold(userData.getLevel())) + userData.getLevel()) * 100) / 100; //*100/100 für Nachkommastellenrundung TODO: passt userData.getLevel() - 1 mit level -1 in Statics.getLevel...
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
			DatabaseWrapper dbw = gson.fromJson(fr, DatabaseWrapper.class);
			serverData = dbw.getServerData();
			userDataList = dbw.getUserDataBase();
			for (UserData userData : userDataList) {
				userData.setUser();
			}
			System.out.println("loaded " + userDataList.size() + " Users and Serverdata from Database");
		} catch(Exception e) {
			System.out.println("loadError: " + e);
			userDataList = new ArrayList<>();
			serverData = new ServerData();
			System.out.println("New Databases for Users and Server created");
		}
	}
	
	public void save(boolean backup) {
		System.out.println("saving Database...");
		String filePath = FILE_PATH;
		serverData.setFlipRoomID(FlipRoom.getFlipRoomID());
		if (backup) {
			System.out.println("creating backup...");
			Format format = new SimpleDateFormat("dd.MM.YY-HH.mm");//backup_25.08.16-19.40.json
			filePath = BACKUP_PATH + format.format(new Date()) + ".json";
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();//.serializeNulls?
		try (FileWriter fw = new FileWriter(filePath)){
			DatabaseWrapper dbw = new DatabaseWrapper();
			dbw.setServerData(serverData);
			dbw.setUserDataBase(userDataList);
			gson.toJson(dbw, fw);
			System.out.println("saved Database");
		} catch (IOException e) {
			System.out.println("IO-Exception, save interrupted: " + e);
		}

	}

	public ServerData getServerData() {
		return serverData;
	}

	public UserData getData(IUser user) {//lieber static?
		if (user == null) throw new IllegalArgumentException("User ist NULL in Database.findUserData!");
		String id = user.getID();
		for (UserData userData : userDataList) {
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
		for (UserData userdata : userDataList) {
			items += userdata + "; ";
		}
		return items + "]";
	}
}
