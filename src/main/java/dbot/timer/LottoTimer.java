package dbot.timer;

import static dbot.Poster.post;

import dbot.UserData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niklas on 29.09.16.
 */
public class LottoTimer extends TimerTask {//3 aus 8 -> zahlen 1-8
	private static final HashMap<UserData, Integer> DATA_MAP = new HashMap<>();//TODO: eigene hashmap machen mit nicen key, value abfragen
	/*private static int first = -1;//TODO: in serverdata abspeichern und laden
	private static int second = -1;
	private static int third = -1;*/
	private static Integer numbers = -1;
	private static boolean isRunning = false;

	public LottoTimer(UserData userData, String params) {
		if (params.equals("last")) {//TODO: regex?
			last();
			return;
		}
		Matcher matcher = match(params);
		if (!matcher.matches()) {
			System.out.println("matcht nicht");
			return;
		}
		Integer tmp = Integer.parseInt(matcher.group(1) + matcher.group(2) + matcher.group(3));
		System.out.println(tmp);
		DATA_MAP.put(userData, tmp);
		if (!isRunning) {
			isRunning = true;
			new Thread(this, "LottoThread").start();
		}
	}

	@Override
	public void run() {
		System.out.println("gestartet");
		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			System.out.println(e);
		}
		numbers = gen();
		System.out.println("gezogene Nummern: " + numbers);


		Iterator it = DATA_MAP.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			if (pair.getValue() == numbers) {
				post(pair.getKey());
			}
			it.remove();
		}


		if (DATA_MAP.containsValue(numbers)) {
			System.out.println("jemand hat gewonnen!");
		} else {
			System.out.println("alle verloren...");
		}
		isRunning = false;
	}

	private static Integer gen() {//TODO: hässlich
		String s = "";
		for (int i = 0; i < 3; i++) {
			s += 1;
			//s += (int)Math.round(Math.random() * 7) + 1;
		}
		return Integer.parseInt(s);
	}

	private static Matcher match(String s) {
		return Pattern.compile("([1-8])\\s?([1-8])\\s?([1-8])").matcher(s);
	}

	static void last() {
		if (numbers < 1) {
			post("es gab noch keine Ziehung!");
		}
		post("Letzte Ziehung: " + numbers);
	}
}
