package dbot.comm;
import dbot.UserData;

import sx.blah.discord.handle.obj.IUser;


public class FlipRoom extends Flip {//flip extenden?
	private IUser uHost;
	private IUser uClient;
	private UserData dHost;
	private UserData dClient;
	private int pot;
	private int roomID;
	private String seite;
	private static int nextID = 1;
	
	protected FlipRoom(IUser uHost, int bet, String seite, UserData dHost) {
		this.uHost = uHost;
		this.dHost = dHost;
		pot = bet;
		roomID = updateID();
		this.seite = seite;
	}
	
	
	protected void join(IUser uClient, UserData dClient) {
		this.uClient = uClient;
		this.dClient = dClient;
		roll();
	}
	
	private void roll() {//schlauer machen
		String flipSeite = null;
		if (Math.random() < 0.5) {
			flipSeite = "TOP";
		} else {
			flipSeite = "KEK";
		}
		if (seite.equals(flipSeite)) {
			pos.post(uHost + " hat mit " + seite + " gegen " + uClient + " gewonnen und bekommt " + (pot * 2) + ":gem:!!");
			dHost.addGems(pot * 2);
		} else {
			pos.post(uClient + " hat mit " + flipSeite + " gegen " + uHost + " gewonnen und bekommt " + (pot * 2) + ":gem:!!");
			dClient.addGems(pot * 2);
		}
	}
	
	private int updateID() {
		return nextID++;
	}
	
	public int getRoomID() {
		return roomID;
	}
	
	public int getPot() {
		return pot;
	}
	
	public IUser getHost() {
		return uHost;
	}
	
	public String getHostID() {
		return uHost.getID();
	}
	
	@Override
	public String toString() {
		return "\nID: " + roomID + " Einsatz: " + pot + " Seite: " + seite + " von: " + uHost;
	}
	
}
