package dbot;

import java.util.List;

/**
 * Created by Niklas on 10.08.2016.
 * Wrapper-Objekt f�r ServerData und UserData
 */
class DatabaseWrapper {
	private ServerData serverData;
	private List<UserData> userData;
	DatabaseWrapper() {}

	ServerData getServerData() {
		return serverData;
	}

	List<UserData> getUserDataBase() {
		return userData;
	}

	void setServerData(ServerData serverData) {
		this.serverData = serverData;
	}

	void setUserDataBase(List<UserData> userData) {
		this.userData = userData;
	}

}
