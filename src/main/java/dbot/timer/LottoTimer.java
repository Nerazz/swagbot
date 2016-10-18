package dbot.timer;

import static dbot.Poster.post;
import static dbot.Poster.edit;

import dbot.DataMap;
import dbot.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niklas on 29.09.16.
 */
public class LottoTimer extends TimerTask {//3 aus 8 -> zahlen 1-8//3 aus 16
	private final static Logger LOGGER = LoggerFactory.getLogger("dbot.timer.LottoTimer");
	private static final String BIG_NUMBERS[] = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};
	private static final int PRICE = 250;
	private static final DataMap<UserData, ArrayList<Integer>> TICKET_MAP = new DataMap<>();
	private static int pot = 10000;//TODO: in serverdata abspeichern und laden
	private static final ArrayList<Integer> LAST_WINS = new ArrayList<>();
	private static boolean isRunning = false;
	private static boolean closed = false;

	public LottoTimer(UserData userData, String params) {
		if (closed) {
			System.out.println("closed");
			post(userData.getName() + " nicht cheaten du Noob...");
			return;
		}

		if (params.equals("last")) {//TODO: regex?
			last();
			return;
		}

		if (params.equals("prize")) {
			post("im Pool: " + pot);
			return;
		}

		if (TICKET_MAP.getKeysOfKey(userData).size() > 2) {//3 Lose max
			post("kauf nicht die ganze Losbude leer, " + userData.getName());
			return;
		}

		if (userData.getGems() < PRICE) {
			post("zu wenig :gem:");
			return;
		}

		Matcher matcher = match(params);
		if (!matcher.matches()) {
			System.out.println("matcht nicht");
			return;
		}

		ArrayList<Integer> guessedList = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			Integer tmp = Integer.parseInt(matcher.group(i));
			if (guessedList.contains(tmp)) {
				System.out.println("Zahl doppelt");
				return;
			}
			guessedList.add(tmp);
		}

		if (matcher.group(4).equals("k")) {
			guessedList.add(-1);
		} else {
			guessedList.add(0);
		}
		Collections.sort(guessedList);

		userData.subGems(PRICE);
		pot += PRICE;
		post("added ticket: " + guessedList);
		TICKET_MAP.put(userData, guessedList);
		if (!isRunning) {
			isRunning = true;
			new Thread(this, "LottoThread").start();
		}
	}

	@Override
	public void run() {//TODO: jeden tag um 8 oder so ziehung
		System.out.println("gestartet");
		try {
			Thread.sleep(15000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		closed = true;
		System.out.println("Pot: " + pot);

		String winPost = "L O T T O Z I E H U N G!!\nGewonnen hat die Nummer:  ";
		Future<IMessage> fWinMessage = post(winPost);
		IMessage winMessage = null;
		try {
			winMessage = fWinMessage.get();
		} catch(InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		gen();
		LAST_WINS.add(-(int)Math.round(Math.random()));//Kinn oder kein Kinn, das ist hier die Frage
		Collections.sort(LAST_WINS);
		System.out.println("gezogene Nummern: " + LAST_WINS);
		for (int i = 1; i < 4; i++) {
			winPost += numberGen(LAST_WINS.get(i)) + ":grey_exclamation:";
			delayEdit(winMessage, winPost);
		}
		if (LAST_WINS.get(0) == -1) {
			winPost += ":white_check_mark:";
		} else {
			winPost += ":x:";
		}
		delayEdit(winMessage, winPost);

		checkNumbersNew();

		winPost += "\n";
		if (TICKET_MAP.containsValue(LAST_WINS)) {
			ArrayList<UserData> winnerList = TICKET_MAP.getKeysOfValue(LAST_WINS);
			int prize = pot / winnerList.size();//TODO: hier verteilung?
			for (UserData userData : winnerList) {
				System.out.println(userData.getName() + " hat gewonnen!");
				userData.addGems(prize);
				winPost += userData.getUser() + " hat gewonnen!!\nHerzlichen Glückwunsch zu deinen " + prize + ":gem:!\n";//TODO: gleiche keys aufaddieren
				post("Du hast beim Lotto " + prize + ":gem: mit der Zahl " + LAST_WINS + " gewonnen!", userData.getUser());
			}
			pot = 10000;
		} else {
			winPost += "Leider hat niemand gewonnen...";
			System.out.println("alle verloren...");
		}
		delayEdit(winMessage, winPost);
		TICKET_MAP.clear();
		isRunning = false;
		closed = false;
	}

	private static void gen() {
		LAST_WINS.clear();
		ArrayList<Integer> poolList = new ArrayList<>();
		for (int i = 1; i < 17; i++) {//aus 16
			poolList.add(i);
		}
		for (int i = 0; i < 3; i++) {
			int picked = (int)(Math.random() * poolList.size());
			//int picked = i; 1 3 5
			LAST_WINS.add(poolList.get(picked));
			poolList.remove(picked);
		}
	}

	private static Matcher match(String s) {
		return Pattern.compile("(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(!k|k)").matcher(s);
	}

	private static void delayEdit(IMessage message, String s) {
		try {
			Thread.sleep(3000);
			edit(message, s);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static String numberGen(int i) {//TODO: global (in poster oder so) verfügbar machen
		System.out.println(i);
		System.out.println("matching: '" + String.valueOf(i) + "'");
		Matcher matcher = Pattern.compile("(\\d)(\\d)?(\\d)?").matcher(String.valueOf(i));//TODO: no match found???
		if (!matcher.matches()) System.out.println("wtf");//TODO: nötig, aber nicht mit wtf...
		String s = BIG_NUMBERS[Integer.parseInt(matcher.group(1))];
		if (matcher.group(2) != null) {
			s += BIG_NUMBERS[Integer.parseInt(matcher.group(2))];
			if (matcher.group(3) != null) {
				s += BIG_NUMBERS[Integer.parseInt(matcher.group(3))];
			}
		}
		System.out.println("parsed: " + s);
		return s;
	}

	/*private static void checkNumbers() {
		int rightNumbers[][] = new int[TICKET_MAP.size()][2];
		for (Integer i : LAST_WINS) {
			System.out.println("i: " + i);
			for (int j = 0; j < TICKET_MAP.size(); j++) {
				System.out.println("j: " + j);
				rightNumbers[j][0] = 0;
				ArrayList<Integer> guessedList = TICKET_MAP.getValue(j);
				for (int k = 0; k < guessedList.size(); k++) {
					System.out.println("k: " + k + ", guessedList(k): " + guessedList.get(k));
					if (k != guessedList.size() - 1 && (int)guessedList.get(k) == i) {
						rightNumbers[j][0] += 1;
					} else {
						if ((int)guessedList.get(k) == i) {
							rightNumbers[j][1] = 1;
						}
					}
				}
				System.out.println("rightNumbers[" + j + "]0 = " + rightNumbers[j][0]);
				System.out.println("rightNumbers[" + j + "]1 = " + rightNumbers[j][1]);

			}
		}

	}*/

	private void checkNumbersNew() {
		//int rightNumbers[][] = new int[TICKET_MAP.size()][2];
		//DataMap<UserData, int[]> rightMap = new DataMap<>();
		//ArrayList<UserData>[] winnerList = new ArrayList[7];
		List<List<UserData>> winnerList = new ArrayList<>(6);//TODO: size 0?, 6 oder 5?
		for (int i = 0; i < 6; i++) {
			winnerList.add(new ArrayList<>());
		}
		System.out.println("size: " + winnerList.size());
		//UserData userData[][] = new UserData[6][];
		for (int i = 0; i < TICKET_MAP.size(); i++) {
			ArrayList<Integer> ticket = TICKET_MAP.getValue(i);
			ticket.retainAll(LAST_WINS);
			System.out.println("rightNumbers: " + ticket);
			if (ticket.isEmpty()) continue;
			int kinn = 0;//TODO: kinn umbenennen
			if (ticket.get(0) == 0 || ticket.get(0) == -1) {
				kinn = 1;//Kinn richtig geraten
				ticket.remove(0);
			}
			kinn += ticket.size() * 2;//für Verteilung in winnerList
			System.out.println("kinn = " + kinn);
			if (kinn >= 2) winnerList.get(kinn - 2).add(TICKET_MAP.getKey(i));//>= 3 für Gewinn?
		}
		System.out.println(winnerList);
		distribute(winnerList);
	}

	private static void last() {
		if (LAST_WINS.isEmpty()) {
			post("es gab noch keine Ziehung!");
			return;
		}
		post("Letzte Ziehung: " + LAST_WINS);
	}

	private static void distribute(final List<List<UserData>> winnerList) {
		final double POT_FACTOR = 0.8;
		final double BON_FACTOR = 1.5;
		//Anzahl Richtige/Kinn      1/0     1/1     2/0     2/1    3/0     3/1
		final double SPART_ANT[] = {0.0072, 0.0145, 0.0435, 0.087, 0.2826, 0.5652};//TODO: 1 richtiger kein gewinn?

		int modPot = (int)(pot * POT_FACTOR);
		int raus = 0;
		double factor[] = new double[SPART_ANT.length];
		int pers = 0;
		for (int i = 0; i < SPART_ANT.length; i++) {
			if (winnerList.get(i).size() > 0) {
				pers += winnerList.get(i).size();
				raus += modPot * SPART_ANT[i];
			}
		}
		pot -= raus;
		raus = 0;
		int bonus;
		int ges[] = new int[SPART_ANT.length];
		int win;
		for (int i = 0; i < SPART_ANT.length; i++) {
			factor[i] = SPART_ANT[i] * (winnerList.get(i).size() / pers);
			bonus = (int)(factor[i] * pot * BON_FACTOR);
			ges[i] = (int)(modPot * SPART_ANT[i]) + bonus;
			raus += bonus * winnerList.get(i).size();
			if (winnerList.get(i).size() > 0) {
				win = (int) (modPot * SPART_ANT[i]) / winnerList.get(i).size() + bonus;
				for (UserData userData : winnerList.get(i)) {
					System.out.println(userData.getName() + " bekommt " + win + ":gem:");
					userData.addGems(win);
				}
			}
		}
		pot -= raus;

	}

}