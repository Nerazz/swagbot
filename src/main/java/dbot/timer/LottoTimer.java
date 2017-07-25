package dbot.timer;

import dbot.Statics;
import dbot.comm.Lotto;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import dbot.sql.impl.UserDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static dbot.util.Poster.buildNum;
import static dbot.util.Poster.edit;
import static dbot.util.Poster.post;

/**
 * handles the lotto drawing
 *
 * @author Niklas Zd
 * @since 29.09.2016
 */
public final class LottoTimer extends Lotto implements Runnable {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.LottoTimer");

	/**
	 * locks entries (and draws winner(eigentlich nicht))TODO: besser
	 */
	@Override
	public void run() {//TODO: jeden tag um 8 oder so ziehung
		closed = true;
		String winPost = "L O T T O Z I E H U N G!!\nGewonnen hat die Nummer:  ";
		Future<IMessage> fWinMessage = post(winPost, Statics.tempBotSpam);
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
		rotateDay(true);
		//Database.getInstance().getServerData().setLastLottoDay(lastDay);TODO: eigentlich kein kommentar, nur wegen DB auskommentiert
		String query = "TRUNCATE TABLE `lotto`";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}

		closed = false;
	}

	/**
	 * draws the winning numbers
	 */
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

	/**
	 * checks for winners
	 */
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

	/**
	 * distributes the won prizes between winners
	 *
	 * @param winnerList list of winners
	 */
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
		post(winMsg, Statics.tempBotSpam);//TODO: für alle
		pot -= raus;
		int toAdd = POT_MAX / 4;
		post("Ich pack mal " + toAdd + ":gem: in den Pot :wink::ok_hand:", Statics.tempBotSpam);
		pot += toAdd;
		//Database.getInstance().getServerData().setLottoPot(pot);//TODO: setLottoPot in db
	}

	/**
	 * adds delay before editing a message
	 *
	 * @param message message to be edited
	 * @param s string the message should become
	 */
	private static void delayEdit(IMessage message, String s) {
		try {
			Thread.sleep(3000);
			edit(message, s);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}