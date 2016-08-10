package dbot;

import java.util.List;

/**
 * Created by Niklas on 10.08.2016.
 */
public class DataBaseWrapper {
	private List<UserData> userData;
	private ServerData serverData;
	DataBaseWrapper() {}

	public List<UserData> getUserDataBase() {
		return userData;
	}

	public ServerData getServerData() {
		return serverData;
	}

	public void setUserDataBase(List<UserData> userData) {
		this.userData = userData;
	}

	public void setServerData(ServerData serverData) {
		this.serverData = serverData;
	}
}
