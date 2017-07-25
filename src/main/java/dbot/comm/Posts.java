package dbot.comm;

import static dbot.util.Poster.buildNum;
import static dbot.util.Poster.post;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dbot.*;
import dbot.sql.SQLData;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import dbot.sql.impl.UserDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * bundled posts parsing and generating
 * loads posts from json on classloading
 *
 * @author Niklas Zd
 * @since 13.09.2016
 */
final class Posts {
	/** logger*/
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Posts");
	/** medal array for top list */
	private static final String medals[] = {":crown:", ":first_place:", ":second_place:", ":third_place:", ":military_medal:"};
	/** strings to load json info in */
	private static String info = "ERROR", changelog = "ERROR", commands = "ERROR", shop = "ERROR", prestigeInfo = "ERROR", planned = "ERROR";

	static {
		String json = "";
		try {
			//System.out.println(new File(Posts.class.getResource("/files").getPath()));
			InputStream is = Posts.class.getResourceAsStream("/files/posts.json");
			try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				String line;
				while ((line = br.readLine()) != null) {
					json += line;
				}
			}
		} catch(IOException e) {
			LOGGER.error("couldn't find posts.json", e);
		}

		JsonObject jObj = new Gson().fromJson(json, JsonObject.class);
		info = getStringFromJson("info", jObj);
		changelog = getStringFromJson("changelog", jObj);
		commands = getStringFromJson("commands", jObj);
		shop = getStringFromJson("shop", jObj);
		prestigeInfo = getStringFromJson("prestigeInfo", jObj);
		planned = getStringFromJson("planned", jObj);

		/*try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			JsonObject jObj = new Gson().fromJson(br, JsonObject.class);
			info = getStringFromJson("info", jObj);
			changelog = getStringFromJson("changelog", jObj);
			commands = getStringFromJson("commands", jObj);
			shop = getStringFromJson("shop", jObj);
			prestigeInfo = getStringFromJson("prestigeInfo", jObj);
			planned = getStringFromJson("planned", jObj);

		} catch(IOException e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * concatenates a JsonArray to one large String
	 *
	 * @param s name of JsonArray
	 * @param jObj simple JsonObject of jsonFile
	 * @return concatenated String
	 */
	private static String getStringFromJson(String s, JsonObject jObj) {//TODO: s und data umbenennen
		JsonArray jArr = jObj.getAsJsonArray(s);
		String data = "";
		for (JsonElement je : jArr) {
			data = data.concat(je.getAsString());
		}
		return data;
	}

	/**
	 * posts stats of user
	 *
	 * @param message to extract author and channel
	 */
	static void stats(IMessage message) {
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		UserData uData = UserDataImpl.getUserData(author);
		String post = "";
		if (uData.getSwagLevel() > 0) post += String.format(" :trident:%s   :sparkles:%s", buildNum(uData.getSwagLevel()), buildNum(uData.getSwagPoints()));
		post += String.format("%nLevel %s (%d/%d exp)", buildNum(uData.getLevel()), uData.getExp(), UserDataImpl.getLevelThreshold(uData.getLevel()));
		post += String.format("%n:gem:%d", uData.getGems());

		if (uData.getPotDur() > 0) {
			post += String.format("%nBoost(x%s) %d min remaining", uData.getFormattedExpRate(), uData.getPotDur());
		} else {
			post += "\nno active boost";
		}
		if (uData.getReminder() != 0) {
			post += String.format("%n%d reminder", Math.abs(uData.getReminder()));
			if (uData.getReminder() > 0) {
				post += " (on)";
			} else {
				post += " (off)";
			}
		}
		post(author + post, channel);
	}

	/**
	 * displays gems of user
	 *
	 * @param message to extract author and channel
	 */
	static void gems(IMessage message) {
		IUser author = message.getAuthor();
		int gems = UserDataImpl.getUserData(author).getGems();
		post(String.format("%s, you have %d:gem:.", author, gems), message.getChannel());//TODO: besser, nicht getData
		//post(author + ", du hast " + UserDataImpl.getData(author, "gems") + ":gem:.", message.getChannel());
	}

	static void test(IMessage message) {//TODO: embed
		EmbedBuilder eb = new EmbedBuilder();
		eb.appendField("title", "testcontent", true);
		eb.withColor(255, 100, 0);
		File file = new File(Posts.class.getResource("/test.png").getPath());
		if (file.exists()) {
			System.out.println("file found: " + file.getAbsoluteFile());
		}
		String path = "attachment://test.png";
		EmbedObject eo =  eb.build();
		eo.image = new EmbedObject.ImageObject(path, null, 128, 128);
		IChannel channel = message.getChannel();
		try {
			channel.sendFile(eo, file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*static void mute(IMessage message) {
		try {
			Statics.GUILD_LIST.getGuild(1).setMuteUser(message.getAuthor(), true);
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statics.GUILD_LIST.getGuild(1).setMuteUser(message.getAuthor(), false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * generates place or medal String from medalArray
	 *
	 * @param i number of medal
	 * @return String with medal, otherwise place
	 */
	private static String medalGen(int i) {
		if (i < 5) {
			return medals[i];
		} else {
			return String.valueOf(i + 1) + ". ";
		}
	}

	/**
	 * generates top message for all guilds
	 *
	 * @param message to extract author and channel
	 */
	static void globalTop(IMessage message) {
		String post = "TOP 5:";
		ArrayList<SQLData> topList = getScoreList();
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserDataImpl.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double) UserDataImpl.getLevelThreshold(uData.getInt("level"));
			post += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		post(post, message.getChannel());
	}

	/**
	 * displays adjacent users with ranks
	 *
	 * @param message to extract author and channel
	 */
	static void rank(IMessage message) {//TODO: global oder local?; bestimmt nice mit sql lösbar
		IUser author = message.getAuthor();
		IChannel channel = message.getChannel();
		ArrayList<SQLData> topList = getScoreList();
		String authorId = author.getID();
		int i = 0;
		while ((i < topList.size()) && (!topList.get(i).getString("id").equals(authorId))) i++;
		String post = "adjacent ranks:";
		if (i != 0) post += "\n" + medalGen(i - 1) + topList.get(i - 1).getString("name") + " - ?";//TODO:!
		post += "\n" + medalGen(i) + topList.get(i).getString("name") + " - ?";
		if (i != topList.size()) post += "\n" + medalGen(i + 1) + topList.get(i + 1).getString("name") + " - ?";
		post(post, channel);
	}

	/**
	 * displays reminder toggle message
	 *
	 * @param message to extract author and channel
	 */
	static void remind(IMessage message) {
		UserData uData = UserDataImpl.getUserData(message.getAuthor());
		uData.negateReminder();
		uData.update();
		post("toggled reminder", message.getChannel());
	}

	/**
	 * prestiges author
	 *
	 * @param message to extract author and channel
	 */
	static void prestige(IMessage message) {
		UserData uData = UserDataImpl.getUserData(message.getAuthor());
		uData.prestige();
	}

	/*static void rank(DataMap<IUser, Double> dataMap, IUser author) {
		int i = 0;
		while ((i < dataMap.size()) && !dataMap.getKey(i).getID().equals(author.getID())) i++;
		String message = "Umgebende Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + dataMap.getKey(i - 1).getName() + " - " + dataMap.getValue(i - 1);
		message += "\n" + medalGen(i) + dataMap.getKey(i) + " - " + dataMap.getValue(i);
		if (i != dataMap.size()) message += "\n" + medalGen(i + 1) + dataMap.getKey(i + 1).getName() + " - " + dataMap.getValue(i + 1);
		post(message);
	}*/

	/**
	 * posts info string
	 *
	 * @param message to extract channel
	 */
	static void info(IMessage message) {
		post(info, message.getChannel());
	}

	/**
	 * posts changelog string
	 *
	 * @param message to extract channel
	 */
	static void changelog(IMessage message) {//TODO: umdrehen? (neuster shit oben?); letzte 3 hauptversionen?
		post(changelog, message.getChannel());
	}

	/**
	 * posts shop string
	 *
	 * @param message to extract channel
	 */
	static void shop(IMessage message) {
		post(shop, message.getChannel());
	}

	/**
	 * posts commands string
	 *
	 * @param message to extract channel
	 */
	static void commands(IMessage message) {
		post(commands, message.getChannel());
	}

	/*static void newCommands(IChannel channel) {
		String content = "```xl\n";
		for(String s : Commands.getCommands()) {
			try {
				content += s + Class.forName("s").getDesc();
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * posts prestigeInfo string
	 *
	 * @param message to extract channel
	 */
	static void prestigeInfo(IMessage message) {
		post(prestigeInfo, message.getChannel());
	}

	/**
	 * posts planned string
	 *
	 * @param message to extract channel
	 */
	static void plan(IMessage message) {
		post(planned, message.getChannel());
	}

	/**
	 * generates local score list
	 *
	 * @param ref reference of guild
	 * @return List of scores
	 */
	private static ArrayList<SQLData> getScoreList(int ref) {
		String strings[] = {"name", "level", "exp"};
		String query = "SELECT `users`.`name`, `level`, `exp` FROM `users` JOIN `guild" + ref + "` AS guild ON `users`.`id` = guild.`id` ORDER BY `level` DESC, `exp` DESC";
		ArrayList<SQLData> dataList = new ArrayList<>();
		try(Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					Object data[] = new Object[3];
					data[0] = resultSet.getObject("name");
					data[1] = resultSet.getObject("level");
					data[2] = resultSet.getObject("exp");
					SQLData sqlData = new SQLData(strings, data);
					dataList.add(sqlData);
				}
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in getScoreList", e);
		}
		return dataList;
	}

	/**
	 * generates score list over all guilds
	 *
	 * @return List of scores
	 */
	private static ArrayList<SQLData> getScoreList() {//TODO: return List<>?; in util oder sql verschieben?
		String strings[] = {"name", "level", "exp"};
		String query = "SELECT `name`, `level`, `exp` FROM `users` ORDER BY `level` DESC, `exp` DESC";
		ArrayList<SQLData> dataList = new ArrayList<>();
		try(Connection conn = SQLPool.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					Object data[] = new Object[3];
					data[0] = resultSet.getObject("name");
					data[1] = resultSet.getObject("level");
					data[2] = resultSet.getObject("exp");
					SQLData sqlData = new SQLData(strings, data);
					dataList.add(sqlData);
				}
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in getScoreList", e);
		}
		return dataList;
	}
}
