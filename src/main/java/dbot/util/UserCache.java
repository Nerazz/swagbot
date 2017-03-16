package dbot.util;

import dbot.util.exception.UserNotFoundException;
import dbot.sql.impl.UserDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Niklas on 16.03.2017.
 */
public class UserCache {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.util.UserCache");
	private static final Map<String, UserCacheObject> USER_CACHE = new HashMap<>();

	private UserCache() {}

	public static UserDataImpl getUserData(IUser user) {
		String id = user.getID();
		synchronized (USER_CACHE) {
			if (!USER_CACHE.containsKey(id)) {
				try {
					UserCacheObject uco = new UserCacheObject(UserDataImpl.loadUserFromDb(user));
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

	public static List<UserDataImpl> getUserData(List<IUser> userList) {
		List<UserDataImpl> userDataList = new ArrayList<>();
		//TODO
	}

	public static boolean containsUser(IUser user) {
		return USER_CACHE.containsKey(user.getID());
	}

	public static void addUser(UserDataImpl userData) {//return boolean success?
		synchronized (USER_CACHE) {
			USER_CACHE.put(userData.getId(), new UserCacheObject(userData));
		}
	}

	public static void setAccessed(IUser user) {
		USER_CACHE.get(user.getID()).setAccessed(true);
	}

	public static void cleanNotAccessed() {
		List<String> keysToDelete = new ArrayList<>();
		for (Map.Entry<String, UserCacheObject> entry : USER_CACHE.entrySet()) {
			if (!entry.getValue().isAccessed()) {
				keysToDelete.add(entry.getKey());
				entry.getValue().setAccessed(false);
			}
		}
		System.out.println("deleting " + keysToDelete.size() + " entries");
		for (String key : keysToDelete) {
			synchronized (USER_CACHE) {
				USER_CACHE.remove(key);
			}
		}
	}

	private static class UserCacheObject {
		private final UserDataImpl userData;
		private boolean accessed = true;

		UserCacheObject(UserDataImpl userData) {
			this.userData = userData;
		}

		UserDataImpl getUserData() {
			return userData;
		}

		boolean isAccessed() {
			return accessed;
		}

		void setAccessed(boolean accessed) {
			this.accessed = accessed;
		}
	}

}
