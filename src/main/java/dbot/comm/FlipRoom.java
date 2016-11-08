package dbot.comm;

import static dbot.Poster.post;

import dbot.Database;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IUser;

public class FlipRoom extends Flip {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.comm.FlipRoom");
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
			afterFlip(dHost, dClient);//TODO: mit elvis-operator machen
		} else {
			afterFlip(dClient, dHost);
		}
	}

	private void afterFlip(UserData winner, UserData looser) {
		winner.addGems(pot * 2);//TODO: pot * 2 bei join zu pot machen
		LOGGER.info("{} won {} Gems vs {}", winner.getName(), pot * 2, looser.getName());
		post(winner + " hat mit " + seite + " gegen " + looser + " gewonnen und bekommt " + (pot * 2) + ":gem:!!");
		post("gz, du hast " + (pot * 2) + ":gem: gegen " + looser.getName() + " gewonnen!", winner.getUser());
		post(":cry: du hast deine " + pot + ":gem: gegen " + winner.getName() + " verloren...", looser.getUser());
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
	//TODO: equals + hashCode
}
