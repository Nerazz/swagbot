package dbot;

import java.util.List;

/**
 * Created by Niklas on 10.08.2016.
 */
class DataBaseWrapper {
	private List<UserData> userData;
	private ServerData serverData;
	DataBaseWrapper() {}

	List<UserData> getUserDataBase() {
		return userData;
	}

	ServerData getServerData() {
		return serverData;
	}

	void setUserDataBase(List<UserData> userData) {
		this.userData = userData;
	}

	void setServerData(ServerData serverData) {
		this.serverData = serverData;
	}
}
