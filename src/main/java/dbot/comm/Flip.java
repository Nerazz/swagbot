package dbot.comm;

import static dbot.Poster.post;
import static dbot.Poster.edit;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;
import java.util.List;
import java.util.*;
import dbot.UserData;

import java.util.regex.*;
import java.util.concurrent.*;

public class Flip {
	private static List<FlipRoom> lRooms = new ArrayList<>();
	private static IMessage roomPost;
	private static String startString = "Offene Flip-Räume:";
	private static String emptyRoomsString = "\n\t\t\t\t\tkeine :sob:";
	
	public Flip() {}

	public static void init(int nextID) {
		FlipRoom.setNextID(nextID);
		Future<IMessage> fMessage = post(startString + emptyRoomsString, -1);
		try {
			roomPost = fMessage.get();
		} catch(InterruptedException|ExecutionException e) {
			System.out.println(e);
		}
		System.out.println("Flip initialized");
	}
	
	void m(UserData uData, String params) {
		IUser author = uData.getUser();
		
		Pattern pattern = Pattern.compile("(\\d+|allin|join|close)(\\s(top|kek|\\d+))?");
		Matcher matcher = pattern.matcher(params);
		
		if (!matcher.matches()) return;

		int bet;
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
					post(author.getName() + ", du hast zu wenig :gem:");
					return;
				} else if (bet < 1) {
					post("nanana, wer macht denn da Scheiße?? :thinking:");
					return;
				} else if (containsUser(author)) {
					post(author.getName() + ", du hast schon einen Raum offen...");
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
	
	private void join(UserData uData, String params) {
		System.out.println(params);
		Pattern pattern = Pattern.compile(".+\\s(\\d+)$");
		Matcher matcher = pattern.matcher(params);
		
		if (!matcher.matches()) {
			System.out.println("matcht nicht in Flip.join");
			return;
		}
		IUser author = uData.getUser();
		int roomID = Integer.parseInt(matcher.group(1));
		FlipRoom gettedRoom = getRoomByID(roomID);
		if (gettedRoom == null) {
			post("Raum " + roomID + " nicht gefunden.");
			return;
		} else if (uData.getGems() < gettedRoom.getPot()) {
			post(author.getName() + ", du hast zu wenig :gem: um beizutreten.");
			return;
		}
		
		uData.subGems(gettedRoom.getPot());
		gettedRoom.join(author, uData);
		lRooms.remove(getRoomIndexByID(roomID));
		postRooms();
	}
	
	private void open(IUser author, int bet, String seite, UserData uData) {
		FlipRoom fRoom = new FlipRoom(author, bet, seite, uData);
		post(author.getName() + " hat neuen Raum um " + fRoom.getPot() + ":gem: geöffnet mit ID: " + fRoom.getRoomID() + " (" + seite + ")");
		lRooms.add(fRoom);
		postRooms();
	}
	
	private void close(IUser author, UserData uData) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getHostID().equals(author.getID())) {
				System.out.println(lRooms.get(i).getPot());
				uData.addGems(lRooms.get(i).getPot());
				post("closing room " + lRooms.get(i).getRoomID());
				lRooms.remove(i);
				postRooms();
			}
		}
		//remove room(author)//FEHLT
	}
	
	void closeAll() {//für bot dc und logout benutzen; TODO: gems werden nicht erstattet?
		for (Iterator<FlipRoom> it = lRooms.iterator(); it.hasNext();) {
			FlipRoom tmpFR = it.next();
			tmpFR.getHostData().addGems(tmpFR.getPot());
			System.out.println("closing room " + tmpFR.getRoomID());
			it.remove();
		}
		postRooms();
		System.out.println("closed all Fliprooms");
	}
	
	private void postRooms() {
		System.out.println("postRooms.start");
		String post = startString;
		int count = 0;
		for (FlipRoom tmpRoom : lRooms) {
			post += tmpRoom.toString();
			count++;
		}
		if (count != 0) {
			edit(roomPost, post);
		} else {
			edit(roomPost, post + emptyRoomsString);
		}
	}

	private boolean containsUser(IUser user) {
		if (user == null) {
			throw new IllegalArgumentException("User darf nicht null sein!");//braucht return?
		}
		for (FlipRoom tmpRoom : lRooms) {
			if (tmpRoom.getHostID().equals(user.getID())) return true;
		}
		return false;
	}
	
	/*private boolean containsRoom(int roomID) {
		for (int i = 0; i < lRooms.size(); i++) {
			if (lRooms.get(i).getRoomID() == roomID) {
				return true;
			}
		}
		return false;
	}*/

	private FlipRoom getRoomByID(int roomID) {
		for (FlipRoom tmpRoom : lRooms) {
			if (tmpRoom.getRoomID() == roomID) return tmpRoom;
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
