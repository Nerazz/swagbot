package dbot.comm;

import dbot.Poster;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;
import java.util.List;
import java.util.*;
import dbot.UserData;

import java.util.regex.*;
import java.util.concurrent.*;

public class Flip {
	private static List<FlipRoom> lRooms = new ArrayList<FlipRoom>();
	protected static Poster pos;
	private static IMessage roomPost;
	private static String startString = "Offene Flip-Räume:";
	private static String emptyRoomsString = "\n\t\t\t\tkeine :sob:";
	
	public Flip() {}
	
	public Flip(Poster pos) {
		System.out.println("flip init!");
		this.pos = pos;
		Future<IMessage> fMessage = pos.post(startString + emptyRoomsString, -1);
		try {
			roomPost = fMessage.get();
		} catch(InterruptedException|ExecutionException e) {
			System.out.println(e);
		}
	}
	
	public void m(UserData uData, String params) {
		IUser author = uData.getUser();
		
		Pattern pattern = Pattern.compile("(\\d+|allin|join|close)(\\s(top|kek|\\d+))?");
		Matcher matcher = pattern.matcher(params);
		
		if (!matcher.matches()) {
			return;
		}
		int bet = -1;
		switch (matcher.group(1)) {
			case "join":
				join(uData, params);
				return;
			case "close":
				close(author, uData);
				return;
			case "allin":
				bet = uData.getGems();
				break;
			default:
				bet = Integer.parseInt(matcher.group(1));
				if (uData.getGems() < bet) {
					pos.post(author + ", du hast zu wenig :gem:");
					return;
				} else if (bet < 1) {
					pos.post("nanana, wer will denn da cheaten?? :thinking:");
					return;
				} else if (containsUser(author)) {
					pos.post(author + ", du hast schon einen Raum offen...");
					return;
				}
				break;
		}
		
		uData.subGems(bet);
		String seite;
		if (matcher.group(3) == null) {
			System.out.println("null in flip");
			if (Math.random() < 0.5) {
				seite = "TOP";
			} else {
				seite = "KEK";
			}
		} else {
			seite = matcher.group(3);
		}
		open(author, bet, seite.toUpperCase(), uData);//TODO:besser machen
	}
	
	public void join(UserData uData, String params) {
		System.out.println(params);
		Pattern pattern = Pattern.compile(".+\\s(\\d+)$");
		Matcher matcher = pattern.matcher(params);
		
		if (!matcher.matches()) {
			System.out.println("matcht nicht in Flip.join");
			return;
		}
		IUser author = uData.getUser();
		int roomID = Integer.parseInt(matcher.group(1));
		FlipRoom gettedRoom = lRooms.get(getRoomIndexByID(roomID));
		if (gettedRoom == null) {
			pos.post("Raum " + roomID + " nicht gefunden.");
			return;
		} else if (uData.getGems() < gettedRoom.getPot()) {
			pos.post(author + ", du hast zu wenig :gem: um beizutreten.");
			return;
		}
		
		uData.subGems(gettedRoom.getPot());
		gettedRoom.join(author, uData);
		lRooms.remove(getRoomIndexByID(roomID));
		postRooms();
	}
	
	private void open(IUser author, int bet, String seite, UserData uData) {
		FlipRoom fRoom = new FlipRoom(author, bet, seite, uData);
		pos.post(author + " hat neuen Raum um " + fRoom.getPot() + ":gem: geöffnet mit ID: " + fRoom.getRoomID() + " (" + seite + ")");
		lRooms.add(fRoom);
		postRooms();
	}
	
	public void close(IUser author, UserData uData) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getHostID().equals(author.getID())) {
				System.out.println(lRooms.get(i).getPot());
				uData.addGems(lRooms.get(i).getPot());
				pos.post("closing room " + lRooms.get(i).getRoomID());
				lRooms.remove(i);
				postRooms();
			}
		}
		//remove room(author)//FEHLT
	}
	
	public void closeAll() {//für bot dc und logout benutzen
		for (int i = 0; i < lRooms.size(); i++) {
			lRooms.remove(i);
		}
		System.out.println("Alle Flipräume geschlossen");
	}
	
	private void postRooms() {
		System.out.println("postRooms.start");
		String post = startString;
		int count = 0;
		for (int i = 0; i < lRooms.size(); i++) {
			post += lRooms.get(i).toString();
			count++;
		}
		if (count != 0) {
			pos.edit(roomPost, post);
		} else {
			pos.edit(roomPost, post + emptyRoomsString);
		}
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
