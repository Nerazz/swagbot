package dbot.timer;

import static dbot.Poster.post;
import static dbot.Poster.edit;
import static dbot.Poster.buildNum;

import dbot.DataMap;
import dbot.UserData;
import dbot.comm.Lotto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niklas on 29.09.16.
 */
public class LottoTimer extends Lotto implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.LottoTimer");

	@Override
	public void run() {//TODO: jeden tag um 8 oder so ziehung
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
			winPost += buildNum(LAST_WINS.get(i)) + ":grey_exclamation:";
			delayEdit(winMessage, winPost);
		}
		if (LAST_WINS.get(0) == -1) {
			winPost += ":white_check_mark:";
		} else {
			winPost += ":x:";
		}
		delayEdit(winMessage, winPost);

		checkNumbers();

		winPost += "\nGewinne wurden verteilt.";
		delayEdit(winMessage, winPost);
		TICKET_MAP.clear();
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
			//picked = i;//1 3 5
			LAST_WINS.add(poolList.get(picked));
			poolList.remove(picked);
		}
	}

	private void checkNumbers() {
		List<List<UserData>> winnerList = new ArrayList<>(6);//TODO: size 0?, 6 oder 5?
		for (int i = 0; i < 6; i++) {
			winnerList.add(new ArrayList<>());
		}
		for (int i = 0; i < TICKET_MAP.size(); i++) {
			ArrayList<Integer> ticket = TICKET_MAP.getValue(i);
			ticket.retainAll(LAST_WINS);
			if (ticket.isEmpty()) continue;
			int score = 0;//TODO: kinn umbenennen
			if (ticket.get(0) == 0 || ticket.get(0) == -1) {
				score = 1;//Kinn richtig geraten
				ticket.remove(0);
			}
			score += ticket.size() * 2;//für Verteilung in winnerList
			if (score >= 2) winnerList.get(score - 2).add(TICKET_MAP.getKey(i));//>= 3 für Gewinn?
		}
		distribute(winnerList);
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
		int win;
		String winMsg = "Gewonnen haben:\n";
		for (int i = 0; i < SPART_ANT.length; i++) {
			if (winnerList.get(i).size() > 0) {
				factor[i] = SPART_ANT[i] * (winnerList.get(i).size() / pers);
				bonus = (int)(factor[i] * pot * BON_FACTOR);
				raus += bonus * winnerList.get(i).size();
				win = (int)((modPot * SPART_ANT[i]) / winnerList.get(i).size()) + bonus;
				for (UserData userData : winnerList.get(i)) {
					System.out.println(userData.getName() + " bekommt " + win + ":gem:");
					winMsg += userData.getName() + ": " + win + ":gem: durch " + (i / 2 + 1) + " Richtige und " + (i % 2) + " Kinn\n";
					userData.addGems(win);
				}
			}
		}
		post(winMsg);
		pot -= raus;

	}

	private static void delayEdit(IMessage message, String s) {
		try {
			Thread.sleep(3000);
			edit(message, s);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}