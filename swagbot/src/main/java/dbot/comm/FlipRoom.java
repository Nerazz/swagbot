package dbot.comm;

import dbot.Poster;
import dbot.UserData;

import sx.blah.discord.handle.obj.IUser;


public class FlipRoom extends Flip {//flip extenden?
	//private static final String top;
	
	private IUser uHost;
	private IUser uClient;
	private UserData dHost;
	private UserData dClient;
	private int pot;
	private int roomID;
	private String seite;
	private static int nextID = 1;
	
	//protected 
	
	protected FlipRoom(IUser uHost, int bet, String seite, UserData dHost) {
		super(pos);
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
		double rnd = Math.random();
		//pos.post("es wurde " + rnd + " gerollt.");
		if (rnd < 0.5) {
			
			if (seite.equals("top")) {
				pos.post(uHost + " hat mit TOP gewonnen!");
				dHost.addGems(pot * 2);
				return;
			}
			else {
				pos.post(uClient + " hat mit TOP gewonnen!");
				dClient.addGems(pot * 2);
				return;
			}
		}
		if (seite.equals("kek")) {
			pos.post(uHost + " hat mit KEK gewonnen!");
			dHost.addGems(pot * 2);
			return;
		}
		else {
			pos.post(uClient + " hat mit KEK gewonnen!");
			dClient.addGems(pot * 2);
			return;
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
	
}
