package dbot.comm;

import dbot.Poster;

import sx.blah.discord.handle.obj.IUser;
import java.util.List;
import java.util.*;
import dbot.UserData;

import java.util.regex.*;

public class Flip {
	
	
	
	private static List<FlipRoom> lRooms = new ArrayList<FlipRoom>();
	protected static Poster pos;
	
	public Flip(Poster pos) {
		this.pos = pos;
	}
	
	public void m(UserData uData, String params) {//unendlichen post mit edit einfuegen
		IUser author = uData.getUser();
		
		Pattern pattern = Pattern.compile("(\\d+|allin|join|close)(\\s(top|kek))?");
		Matcher matcher = pattern.matcher(params);
		
		if (!matcher.matches()) {
			return;
		}
		int bet = -1;
		switch (matcher.group(1)) {
			case "join":
				join(uData, params);
				break;
			case "close":
				close(author, uData);
				break;
			case "allin":
				bet = uData.getGems();
				break;
			default:
				bet = Integer.parseInt(matcher.group(1));
				break;
		}
		
		if (bet < 1) {
			return;
		}

		//--------------bis hier
		
		
		
		/*String seite = (String)paramList.get(2).getValue();
		if (arg.equals("close")) {
			System.out.println("close");
			close(author, uData);
		}
		else if (!containsUser(author)) {
			int bet = Integer.parseInt(arg);
			if ((uData.getGems() < bet) || (bet < 1)) {
				pos.post("zu wenig :gem:");
				return;
			}
			if (seite.equals("top") || seite.equals("kek")) {
				uData.subGems(bet);
				open(author, bet, seite, uData);
			}
			else {
				double rnd = Math.random();
				if (rnd < 0.5) {
					uData.subGems(bet);
					open(author, bet, "top", uData);
				}
				else {
					uData.subGems(bet);
					open(author, bet, "kek", uData);
				}
			}
			
		}
		
		else {
			pos.post("schon vorhanden");
		}*/
	}
	
	public void join(UserData uData, String params) {
		/*Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(params);
		try {
			System.out.println(roomID);
			int iRoomID = Integer.parseInt(roomID);
			FlipRoom gettedRoom = lRooms.get(getRoomIndexByID(iRoomID));
			if (gettedRoom != null) {
				if (uData.getGems() < gettedRoom.getPot()) {
					pos.post("zu wenig :gem:");
					return;
				}
				uData.subGems(gettedRoom.getPot());
				gettedRoom.join(author, uData);
				lRooms.remove(getRoomIndexByID(iRoomID));
				//pos.post(winner + " hat den Pot gewonnen!");
			}
			
			else {
				pos.post("Room-ID nicht gefunden.");
			}
		} catch(Exception e) {
			System.out.println("parseerror (flip.join)");
		}*/
	}
	
	private void open(IUser author, int bet, String seite, UserData uData) {
		FlipRoom fRoom = new FlipRoom(author, bet, seite, uData);
		pos.post(author + " hat neuen Raum um " + fRoom.getPot() + ":gem: geöffnet mit ID: " + fRoom.getRoomID() + " (" + seite.toUpperCase() + ")", 600000);//bestehenden Post posten(ggf. editieren)
		lRooms.add(fRoom);
	}
	
	public void close(IUser author, UserData uData) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getHostID().equals(author.getID())) {
				System.out.println(lRooms.get(i).getPot());
				uData.addGems(lRooms.get(i).getPot());
				pos.post("closing room " + lRooms.get(i).getRoomID());
				lRooms.remove(i);
				
				System.out.println("removed room");
			}
		}
		//remove room(author)//FEHLT
	}
	
	public void closeAll() {
		for (int i = 0; i < lRooms.size(); i++) {
			//logik
		}
		System.out.println("Alle Flipräume geschlossen");
	}
	
	private boolean containsUser(IUser user) {
		if (user == null) {
			throw new IllegalArgumentException("User darf nicht null sein!");//braucht return?
		}
		for (int i = 0; i < lRooms.size(); i++) {
				if (lRooms.get(i).getHostID().equals(user.getID())) {
					return true;
				}
			}
		return false;
	}
	
	private boolean containsRoom(int roomID) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getRoomID() == roomID) {
				return true;
			}
		}
		return false;
	}
	
	private FlipRoom getRoomByID(int roomID) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getRoomID() == roomID) {
				return lRooms.get(i);
			}
		}
		return null;
	}
	
	private int getRoomIndexByID(int roomID) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getRoomID() == roomID) {
				return i;
			}
		}
		return -1;
	}
}
