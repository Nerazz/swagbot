package dbot.util;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.util.exception.UserNotFoundException;
import dbot.sql.impl.UserDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache for UserData
 *
 * @author Niklas Zd
 * @since 16.03.2017
 */
public class UserCache {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.util.UserCache");
	/** Hashmap with id of user as key and a userdata wrapper object as value */
	private static final Map<Long, UserCacheObject> USER_CACHE = new HashMap<>();
	/** query to select a single user and its from database */
	private static final String SELECT_QUERY = "SELECT `ticks`, `lastSeen`, `gems`, `level`, `exp`, `swagLevel`, `swagPoints`, `reminder`, `expRate`, `potDur` FROM `users` WHERE `id` = ?";

	private UserCache() {}

	/**
	 * gets user from cache or adds it if nonexistent
	 *
	 * @param user user to be retrieved
	 * @return retrieved userdata
	 */
	public static UserDataImpl getUserData(IUser user) {//TODO: add if nonexistent
		long id = user.getLongID();//autoboxen lassen? oder direkt Long?
		synchronized (USER_CACHE) {
			if (!USER_CACHE.containsKey(id)) {
				try {
					UserCacheObject uco = new UserCacheObject(loadUserFromDb(user));
					USER_CACHE.put(id, uco);
				} catch(UserNotFoundException e) {
					e.printStackTrace();//TODO: log
				}
			}
			UserCacheObject uco = USER_CACHE.get(id);
			uco.setAccessed(true);
			return uco.getUserData();
		}
	}

	/**
	 * gets a list of users from cache and adds all nonexistent users
	 * @param userList requested users
	 * @return list of userdata
	 */
	public static List<UserDataImpl> getUserData(List<IUser> userList) {
		List<UserDataImpl> userDataList = new ArrayList<>();
		//TODO
		return userDataList;
	}

	/**
	 * checks if user exists in cache
	 *
	 * @param user user to check
	 * @return true if user is contained
	 */
	public static boolean containsUser(IUser user) {
		return USER_CACHE.containsKey(user.getLongID());
	}

	/**
	 * adds userdata to cache
	 *
	 * @param userData userdata to be added
	 */
	private static void addUser(UserDataImpl userData) {//return boolean success?
		synchronized (USER_CACHE) {
			USER_CACHE.put(userData.getId(), new UserCacheObject(userData));
		}
	}

	/**
	 * sets accessed in cache object to true
	 * @param user user that was accessed
	 */
	public static void setAccessed(IUser user) {
		USER_CACHE.get(user.getLongID()).setAccessed(true);
	}

	/**
	 * clears not accessed users since last call to this method and sets accessed to false otherwise
	 */
	public static void cleanNotAccessed() {
		List<Long> keysToDelete = new ArrayList<>();
		for (Map.Entry<Long, UserCacheObject> entry : USER_CACHE.entrySet()) {
			if (!entry.getValue().isAccessed()) {
				keysToDelete.add(entry.getKey());
			} else {
				entry.getValue().setAccessed(false);
			}
		}
		System.out.println("deleting " + keysToDelete.size() + " entries");
		for (Long key : keysToDelete) {
			synchronized (USER_CACHE) {
				USER_CACHE.remove(key);
			}
		}
	}

	/**
	 * loads userdata of a user from database to cache
	 *
	 * @param user user to be loaded
	 * @return loaded userdata
	 * @throws UserNotFoundException if user wasn't found in database
	 */
	private static UserDataImpl loadUserFromDb(IUser user) throws UserNotFoundException {//TODO: return void?
		long id = user.getLongID();
		try (Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_QUERY)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				String insertQuery = "INSERT INTO users (id, name, lastSeen) VALUES (?, ?, ?)";
				try (PreparedStatement psInsert = con.prepareStatement(insertQuery)) {
					psInsert.setLong(1, id);
					psInsert.setString(2, user.getName());
					psInsert.setLong(3, Instant.now().toEpochMilli());
					psInsert.executeUpdate();
					con.commit();
				} catch(SQLException e) {
					e.printStackTrace();//TODO: log
				}
				rs = ps.executeQuery();
				rs.next();
			}
			return new UserDataImpl(
					user,
					rs.getInt("ticks"),
					rs.getLong("lastSeen"),
					rs.getInt("gems"),
					rs.getInt("level"),
					rs.getInt("exp"),
					rs.getInt("swagLevel"),
					rs.getInt("swagPoints"),
					rs.getInt("reminder"),
					rs.getInt("expRate"),
					rs.getInt("potDur"));
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}
		throw new UserNotFoundException();
	}

	/**
	 * loads multiple users from database to cache
	 *
	 * @param usersToLoad users to be loaded
	 */
	public static void loadUsersFromDb(List<IUser> usersToLoad) {
		String selectQuery = "SELECT `id`, `ticks`, `lastSeen`, `gems`, `level`, `exp`, `swagLevel`, `swagPoints`, `reminder`, `expRate`, `potDur` FROM `users` WHERE `id` IN (";
		StringBuilder sb = new StringBuilder();
		sb.append(selectQuery);
		for (IUser user : usersToLoad) {
			//selectQuery += user.getID() + ", ";
			sb.append(user.getLongID()).append(", ");
		}
		//selectQuery = selectQuery.substring(0, selectQuery.length() - 2).concat(")");
		sb.delete(sb.length() - 2, sb.length()).append(")");
		selectQuery = sb.toString();
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(selectQuery)) {
			ResultSet rs = ps.executeQuery();
			UserDataImpl userData;
			while (rs.next()) {
				userData = new UserDataImpl(
						Statics.BOT_CLIENT.getUserByID(rs.getLong("id")),//TODO: bestimmt besser möglich (user gab es vorher)
						rs.getInt("ticks"),
						rs.getLong("lastSeen"),
						rs.getInt("gems"),
						rs.getInt("level"),
						rs.getInt("exp"),
						rs.getInt("swagLevel"),
						rs.getInt("swagPoints"),
						rs.getInt("reminder"),
						rs.getInt("expRate"),
						rs.getInt("potDur"));
				UserCache.addUser(userData);//TODO: vielleicht besser möglich mit new list -> map.addAll oder lambdas?
			}
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}
	}

	/**
	 * Wrapper for userdata, stores if the data was accessed
	 */
	private static class UserCacheObject {
		/** userdata */
		private final UserDataImpl userData;
		/** if the data was accessed */
		private boolean accessed = true;

		/**
		 * Creates new UserCacheObject
		 *
		 * @param userData userdata to be wrapped
		 */
		UserCacheObject(UserDataImpl userData) {
			this.userData = userData;
		}

		/**
		 * @return userdata
		 */
		UserDataImpl getUserData() {
			return userData;
		}

		/**
		 * @return was accessed
		 */
		boolean isAccessed() {
			return accessed;
		}

		/**
		 * @param accessed sets accessed
		 */
		void setAccessed(boolean accessed) {
			this.accessed = accessed;
		}
	}

}
