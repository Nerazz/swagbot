package dbot;

import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IGuild;
import java.util.List;
import java.util.*;

public class DataBase {//soll eigentlich static sein
	
	private static int size = 0;
	private static final List<UserData> lUDB = new ArrayList<UserData>();
	public static final ServerData SD = new ServerData();//lieber mit getter durchreichen?
	private static IGuild guild;
	protected final static int[] rpgLevelThreshold = new int[100];
	
	public DataBase(IGuild guild) {
		this.guild = guild;
		for (int i = 0; i < 100; i++) {
			rpgLevelThreshold[i] = i * 80 + 1000;
			//System.out.print("Level " + (i + 1) + " = " + rpgLevelThreshold[i] + "\t|| ");
		}
		System.out.println("DB vorbereitet");
	}
	
	public DataBase() {
		
	}
	
	public void add(IUser user) {
		if (!containsUser(user)) {
			UserData uData = new UserData(user);
			lUDB.add(uData);
			size++;//lieber = lDB.size() setzen?
		}
		else {
			System.out.println(user.getName() + " ist in DB schon vorhanden!");
		}
	}
	
	public void add(UserData uData) {
		lUDB.add(uData);
		size++;
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
	
	/*public void save() {
		System.out.println("save start");
		Gson gson = new Gson();
		gson.toJson(1);
		gson.toJson("test");
		gson.toJson("abc");
	}
	
	public void load() {
		System.out.println("load start");
		int one = gson.fromJson("1", int.class);
		System.out.println(one);
		
	}*/
	
	
	public void load() {//filenotfound, ... exceptions
		try {
			System.out.println("load start");
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("./userdatabase.json"));
			JSONArray jobj = (JSONArray) obj;
			JSONObject jtest = new JSONObject();
			Iterator<JSONObject> iterator = jobj.iterator();
			
			UserData uData;
			
			while (iterator.hasNext()) {
				jtest = (JSONObject)iterator.next();
				String ID = (String)jtest.get("ID");
				try {
					IUser user = guild.getUserByID(ID);
					if (!containsUser(user)) {
						//String name = (String)jtest.get("name");
						uData = new UserData(user);
						uData.setGems((int)((long)jtest.get("gems")));
						uData.setExp((int)((long)jtest.get("rpgExp")));
						uData.setLevel((int)((long)jtest.get("rpgLevel")));
						uData.setPresLevel((int)((long)jtest.get("rpgPresLevel")));
						uData.setrpgClass((String)jtest.get("rpgClass"));
				
						this.add(uData);
					} else {
						System.out.println(user.getName() + " ist schon vorhanden.");
					}
					
				} catch(Exception de) {//zu discordexception machen oder was immer das ist
					System.out.println(de);
					System.out.println("User mit ID: " + ID + " nicht gefunden.");
				}
				
			}
			System.out.println(size + " User aus userDB geladen");
			
		} catch(Exception e) {
			System.out.println("LOADERROR IN DATABASE");
		}
	}
	
	public void save() {
		System.out.println("save start");
		//JSONObject oUser = new JSONObject();//unwichtig
		JSONObject userArray = new JSONObject();//array fuer userobjekte mit infos
		JSONArray jArray = new JSONArray();
		JSONObject[] objs = new JSONObject[size];
		
		//JSONObject test = new JSONObject();
		
		for (int i = 0; i < size; i++) {
			objs[i] = new JSONObject();
		}
		for (int i = 0; i < size; i++) {
			//JSONObject obj = new JSONObject();
			UserData tUData = lUDB.get(i);
			objs[i].put("ID", tUData.getID());
			//objs[i].put("user", tData.getUser());
			objs[i].put("name", tUData.getName());
			objs[i].put("gems", tUData.getGems());
			objs[i].put("rpgClass", tUData.getrpgClass());
			objs[i].put("rpgExp", tUData.getExp());
			objs[i].put("rpgLevel", tUData.getLevel());
			objs[i].put("rpgPresLevel", tUData.getPresLevel());
			jArray.add(objs[i]);
			//System.out.println(objs[i].toString());
		}
		//test.put("rpg", jArray);
		
		try {
			File oFile = new File("./userdatabase.json");
			if (!oFile.exists()) {
				oFile.createNewFile();
				System.out.println("file created");
			}
			FileWriter fw = new FileWriter(oFile/*.getAbsoluteFile()*/);
			BufferedWriter bw = new BufferedWriter(fw);
			//for (int i = 0; i < size; i++) {
			//	bw.write(jArray.toJSONString());
				//bw.write(jobj.toJSONString());
				//bw.newLine();
			//}
			bw.write(jArray.toJSONString());
			//bw.write(test.toJSONString());
			
			bw.close();
			System.out.println("Savefile fertig geschrieben");
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		/*try {
		File oFile = new File("./test.json");
			if (!oFile.exists()) {
				oFile.createNewFile();
				System.out.println("file created");
			}
		
		//FileWriter fw = new FileWriter(oFile/*.getAbsoluteFile());
		
		} catch(Exception e) {
		}
		
		obj.put("name", "niklas");
		obj.put("ID", new Integer(123123));
		obj.put("gems", "533");
		
		System.out.println(obj);
		
		try {
			FileWriter fw = new FileWriter("./test.json");
			fw.write(obj.toJSONString());
			
			System.out.println("json created");
			
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(obj.toJSONString() + "nice");
			fw.flush();
			fw.close();
			bw.close();
			System.out.println("fertig fw");
		} catch(Exception e) {
			
		}*/
		
	}
	
	public boolean containsUser(IUser user) {
		if (user == null) {
			//throw new NullUserException("User darf nicht null sein!");
			throw new IllegalArgumentException("User darf nicht null sein!");//braucht return?
		}
		for (int i = 0; i < size; i++) {
				if (lUDB.get(i).getID() == user.getID()) {
					return true;
				}
			}
		return false;
	}
	
	public UserData getData(IUser user) {
		if ((!this.containsUser(user)) || (user == null)) {//optimieren! bei kontrolle wird gesuchtes objekt bereits gefunden!
			System.out.println("USER FAIL? CODE PRUEFEN!!");
			return null;
		}
		Iterator<UserData> iterator = lUDB.iterator();
		while (iterator.hasNext()) {
			UserData foundData = iterator.next();
			if (foundData.getID() == user.getID()) {
				return foundData;
			}
		}
		return null;
	}
	
	/*@Override
	public String toString() {
		String items = "items: [";
		for (int i = 0; i < size; i++) {
			items += lUDB.get(i).getName() + ", ";
		}
		return items + "]";
	}*/
}
