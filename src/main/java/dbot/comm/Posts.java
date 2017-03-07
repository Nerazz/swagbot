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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Niklas on 13.09.2016.
 */
final class Posts {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.Posts");
	private static final String medals[] = {"crown", ":first_place:", ":second_place:", ":third_place:", ":military_medal:"};
	private static String info = "ERROR", changelog = "ERROR", commands = "ERROR", shop = "ERROR", prestigeInfo = "ERROR";

	static {
		String fileName = "files/posts.json";
		URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
		if (url == null) {
			LOGGER.error("Couldn't find URL");
			System.exit(-1);
		}
		String filePath = url.getFile();
		System.out.println("Filepath: " + filePath);
		File file = new File(filePath);
		if (file.exists()) {
			LOGGER.info("found posts.json");
		} else {
			LOGGER.error("posts.json not found");
			try {
				Statics.BOT_CLIENT.logout();
			} catch(DiscordException e) {
				LOGGER.error("ERROR logging out", e);
			}
			System.exit(-1);
		}
		try(BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
			JsonObject jObj = new Gson().fromJson(br, JsonObject.class);
			info = getStringFromJson("info", jObj);
			changelog = getStringFromJson("changelog", jObj);
			commands = getStringFromJson("commands", jObj);
			shop = getStringFromJson("shop", jObj);
			prestigeInfo = getStringFromJson("prestigeInfo", jObj);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static String getStringFromJson(String s, JsonObject jObj) {//TODO: s und data umbenennen
		JsonArray jArr = jObj.getAsJsonArray(s);
		String data = "";
		for (JsonElement je : jArr) {
			data = data.concat(je.getAsString());
		}
		return data;
	}

	static void stats(IMessage message) {
		IUser user = message.getAuthor();
		IChannel channel = message.getChannel();
		UserData uData = new UserData(user, 255);//exp, level, expRate, potDur, swagLevel, swagPoints, reminder
		String post = "";
		if (uData.getSwagLevel() > 0) post += String.format(" :trident:%s   :sparkles:%s", buildNum(uData.getSwagLevel()), buildNum(uData.getSwagPoints()));
		post += String.format("%nLevel %s (%d/%d Exp)", buildNum(uData.getLevel()), uData.getExp(), UserData.getLevelThreshold(uData.getLevel()));
		post += String.format("%n%d:gem:", uData.getGems());

		if (uData.getPotDuration() > 0) {
			post += String.format("%nBoost(x%s) ist noch %d min aktiv", uData.getFormattedExpRate(), uData.getPotDuration());
		} else {
			post += "\nKein aktiver Boost";
		}
		if (uData.getReminder() != 0) {
			post += String.format("%n%d Reminder", Math.abs(uData.getReminder()));
			if (uData.getReminder() > 0) {
				post += " (on)";
			} else {
				post += " (off)";
			}
		}
		post(user + post, channel);
	}

	static void gems(IMessage message) {
		IUser author = message.getAuthor();
		post(author + ", du hast " + UserData.getData(author, "gems") + ":gem:.", message.getChannel());
	}

	private static String medalGen(int i) {
		if (i < 5) {
			return medals[i];
		} else {
			return String.valueOf(i + 1) + ". ";
		}
	}

	static void globalTop(IMessage message) {
		String post = "TOP 5:";
		ArrayList<SQLData> topList = getScoreList();
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserData.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double)UserData.getLevelThreshold(uData.getInt("level"));
			post += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		post(post, message.getChannel());
	}

	static void localTop(IMessage message) {
		IChannel channel = message.getChannel();
		String post = "TOP 5:";
		int ref = Statics.GUILD_LIST.getRef(channel.getGuild());
		ArrayList<SQLData> topList = getScoreList(ref);
		for (int i = 0; (i < topList.size()) && (i < 5); i++) {
			SQLData uData = topList.get(i);
			//double score = ((Integer)data.get("level")).doubleValue() + Math.floor(((Integer)data.get("exp")).doubleValue() / (double)UserData.getLevelThreshold((Integer)data.get("level")) * 100) / 100;
			double score = uData.getInt("level") + (double)uData.getInt("exp") / (double)UserData.getLevelThreshold(uData.getInt("level"));
			post += "\n" + medalGen(i) + uData.getString("name") + " - " + String.format("%.2f", score);
		}
		post(post, channel);
	}

	static void rank(IUser author, IChannel channel) {//TODO: global oder local?; bestimmt nice mit sql lösbar
		ArrayList<SQLData> topList = getScoreList();
		String authorId = author.getID();
		int i = 0;
		while ((i < topList.size()) && (!topList.get(i).getString("id").equals(authorId))) i++;
		String message = "Umgebende Ränge:";
		if (i != 0) message += "\n" + medalGen(i - 1) + topList.get(i - 1).getString("name") + " - ?";//TODO:!
		message += "\n" + medalGen(i) + topList.get(i).getString("name") + " - ?";
		if (i != topList.size()) message += "\n" + medalGen(i + 1) + topList.get(i + 1).getString("name") + " - ?";
		post(message, channel);
	}

	static void remind(IMessage message) {
		UserData uData = new UserData(message.getAuthor(), 128);//reminder
		uData.negateReminder();
		uData.update();
		post("Reminder getogglet", message.getChannel());
	}

	static void prestige(IMessage message) {
		UserData uData = new UserData(message.getAuthor(), 101);//gems, level, SL, SP
		uData.prestige(Statics.GUILD_LIST.getRef(message.getGuild()));
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

	static void info(IMessage message) {
		post(info, message.getChannel());
	}

	static void changelog(IMessage message) {//TODO: umdrehen? (neuster shit oben?); letzte 3 hauptversionen?
		post(changelog, message.getChannel());
	}

	static void shop(IMessage message) {
		post(shop, message.getChannel());
	}

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

	static void prestigeInfo(IMessage message) {
		post(prestigeInfo, message.getChannel());
	}

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
