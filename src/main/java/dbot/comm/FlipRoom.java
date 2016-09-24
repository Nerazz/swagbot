package dbot.comm;

import static dbot.Poster.post;

import dbot.Database;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

public class FlipRoom extends Flip {
	private static final Logger logger = LoggerFactory.getLogger("dbot.comm.FlipRoom");
	private IUser uHost;
	private IUser uClient;
	private UserData dHost;
	private UserData dClient;
	private int pot;
	private int roomID;
	private String seite;
	private static int currentID = Database.getInstance().getServerData().getFlipRoomID();
	
	FlipRoom(IUser uHost, int bet, String seite, UserData dHost) {
		this.uHost = uHost;
		this.dHost = dHost;
		pot = bet;
		roomID = updateID();
		this.seite = seite;
	}
	
	
	void join(IUser uClient, UserData dClient) {
		this.uClient = uClient;
		this.dClient = dClient;
		roll();
	}
	
	private void roll() {
		String flipSeite;
		if (Math.random() < 0.5) {
			flipSeite = "TOP";
		} else {
			flipSeite = "KEK";
		}
		if (seite.equals(flipSeite)) {//TODO: noch verbesserbar mit extamethode?
			dHost.addGems(pot * 2);
			logger.info("{} won {} Gems vs {}", dHost.getName(), pot * 2, dClient.getName());
			post(uHost + " hat mit " + seite + " gegen " + uClient + " gewonnen und bekommt " + (pot * 2) + ":gem:!!");
			post("gz, du hast " + (pot * 2) + ":gem: gegen " + uClient.getName() + " gewonnen!", uHost);
			post(":cry: du hast deine " + pot + ":gem: gegen " + uHost.getName() + " verloren...", uClient);
		} else {
			dClient.addGems(pot * 2);
			logger.info("{} won {} Gems vs {}", dClient.getName(), pot * 2, dHost.getName());
			post(uClient + " hat mit " + flipSeite + " gegen " + uHost + " gewonnen und bekommt " + (pot * 2) + ":gem:!!");
			post("gz, du hast " + (pot * 2) + ":gem: gegen " + uHost.getName() + " gewonnen!", uClient);
			post(":cry: du hast deine " + pot + ":gem: gegen " + uClient.getName() + " verloren...", uHost);
		}
	}
	
	private int updateID() {
		return currentID++;
	}//TODO: (noch nötig?) sollte dann ++currentID (nach serverData load-implementation)
	
	int getRoomID() {
		return roomID;
	}
	
	int getPot() {
		return pot;
	}
	
	IUser getHost() {
		return uHost;
	}

	UserData getHostData() {
		return dHost;
	}
	
	String getHostID() {
		return uHost.getID();
	}

	public static int getFlipRoomID() {
		return currentID;
	}
	
	@Override
	public String toString() {
		return "\nID»'" + roomID + "' Einsatz»'" + pot + "' Seite»'" + seite + "' Host»'" + uHost.getName() + "'";
	}
	
}
