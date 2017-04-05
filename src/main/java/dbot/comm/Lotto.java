package dbot.comm;

import dbot.Statics;
import dbot.sql.SQLPool;
import dbot.sql.UserData;
import dbot.sql.impl.UserDataImpl;
import dbot.timer.LottoTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import dbot.util.DataMap;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dbot.util.Poster.post;

/**
 * lotto main class
 *
 * @author Niklas Zd
 * @since 19.10.2016
 */
public class Lotto {
	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.LottoTimer");
	//protected static int lastDay = Database.getInstance().getServerData().getLastLottoDay();//1 = Monday, 7 = Sunday
	/** most recent day of drawing (1(monday) - 7(sunday)) */
	protected static int lastDay = 1;
	/** maximal pot for bot to increase itself (tickets still go in) */
	protected static final int POT_MAX = 420000;
	/** price of one ticket */
	private static final int PRICE = 250;
	/** Map for userdata - ticketList */
	protected static final DataMap<UserData, ArrayList<Integer>> TICKET_MAP = new DataMap<>();//id, arraylist?
	//protected static int pot = Database.getInstance().getServerData().getLottoPot();
	/** value of pot */
	protected static int pot = 420000;
	/** list of last chosen numbers */
	protected static final ArrayList<Integer> LAST_WINS = new ArrayList<>();
	/** status for entries */
	protected static boolean closed = false;

	/*private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
	private static final LottoTimer LOTTO_TIMER = new LottoTimer();*/
	/*static {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		//LottoTimer lottoTimer = new LottoTimer();
		long initDelay = Duration.between(LocalDateTime.now(), getNextDrawing()).getSeconds();
		try {
			executor.scheduleAtFixedRate(LOTTO_TIMER, initDelay, 60 * 60 * 24 * 5, TimeUnit.SECONDS);//rate: alle 5 Tage
		} catch(Exception e) {
			LOGGER.error("lottoTimer RIP!!", e);
		}/*
		//TODO: berechnung von initdelay (mit serverdata)
		//LocalDateTime nextLotto = LocalDateTime.of(2016, Month.OCTOBER, 19, 15, 52);
		LocalDateTime nextLotto = getNextDrawing();

		System.out.println(nextLotto);
		long initDelay = Duration.between(LocalDateTime.now(), nextLotto).getSeconds();
		System.out.println(initDelay);
		SCHEDULER.scheduleAtFixedRate(LOTTO_TIMER, initDelay, 60 , SECONDS);//alle 5 Tage in sec: 60 * 60 * 24 * 5*/
	//}

	/**
	 * loads tickets from database to ram (for reloading last tickets on bot startup)
	 */
	public static void init() {
		//TODO: load tickets from db
		String query = "SELECT * FROM `lotto`";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ResultSet rs = ps.executeQuery();
			String ownerID;
			int first, second, third, kinn;
			while (rs.next()) {
				ownerID = rs.getString("ownerID");
				first = rs.getInt("first");
				second = rs.getInt("second");
				third = rs.getInt("third");
				kinn = rs.getInt("kinn");
				ArrayList<Integer> guessedList = new ArrayList<>();
				guessedList.add(kinn);
				guessedList.add(first);
				guessedList.add(second);
				guessedList.add(third);
				UserData userData = UserDataImpl.getUserData(Statics.BOT_CLIENT.getUserByID(ownerID));
				TICKET_MAP.put(userData, guessedList);
			}
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		LottoTimer lottoTimer = new LottoTimer();
		long initDelay = Duration.between(LocalDateTime.now(), getNextDrawing()).getSeconds();
		try {
			executor.scheduleAtFixedRate(lottoTimer, initDelay, 60 * 60 * 24 * 5, TimeUnit.SECONDS);//rate: alle 5 Tage
		} catch(Exception e) {
			LOGGER.error("lottoTimer RIP!!", e);
		}
	}

	/**
	 * filters the message for sub commands
	 *
	 * @param message to extract content
	 */
	static void main(IMessage message) {
		String content = message.getContent().toLowerCase();
		Matcher matcher = Pattern.compile("^!lotto\\s(last|pool|next|(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(!k|k))$").matcher(content);
		if (!matcher.matches()) {
			System.out.println("matcher matcht nicht!");//TODO: log
			return;
		}
		IChannel channel = message.getChannel();

		switch (matcher.group(1)) {//TODO: default?
			case "last":
				last();
				return;

			case "pool":
				post(String.format("%d:gem: are in the pool", pot), channel);
				return;

			case "next":
				LocalDateTime next = getNextDrawing();
				post(String.format("Next drawing will take place %s", next.format(DateTimeFormatter.ofPattern("dd.MM. HH:mm"))), channel);//TODO: geht bestimmt besser mit %t
				return;
		}

		IUser author = message.getAuthor();
		UserData userData = UserDataImpl.getUserData(author);

		if (closed) {
			System.out.println("closed");
			post(userData.getName() + " lotto is already closed", channel);//TODO: bessere nachricht
			return;
		}

		String query = "SELECT COUNT(*) FROM `lotto` WHERE `ownerID` = ?";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) { //TODO: connection besser machen, auch zum adden von tickets benutzen
			ps.setString(1, userData.getId());
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1) >= 3) {//3 lose max
				post("zu viele Tickets!", channel);//TODO: bessere errornachricht
				return;
			}
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}

		if (userData.getGems() < PRICE) {
			post(String.format("%s, you don't have enough :gem:.", author), channel);
			return;
		}

		/*ADD TICKET LOGIK*/
		ArrayList<Integer> guessedList = new ArrayList<>();
		for (int i = 2; i < 5; i++) {
			Integer tmp = Integer.parseInt(matcher.group(i));
			if (guessedList.contains(tmp)) {
				System.out.println("Zahl doppelt");//TODO: post mit error
				return;
			}
			guessedList.add(tmp);
		}

		if (matcher.group(5).equals("k")) {
			guessedList.add(-1);
		} else {
			guessedList.add(0);
		}
		Collections.sort(guessedList);
		userData.subGems(PRICE);
		pot += PRICE;

		query = "INSERT INTO `lotto` (`ownerID`, `first`, `second`, `third`, `kinn`) VALUES (?, ?, ?, ?, ?)";
		try(Connection con = SQLPool.getDataSource().getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
			ps.setString(1, userData.getId());
			ps.setInt(2, guessedList.get(1));
			ps.setInt(3, guessedList.get(2));
			ps.setInt(4, guessedList.get(3));
			ps.setInt(5, guessedList.get(0));
			ps.executeUpdate();//TODO: int für errormsg
			con.commit();
			TICKET_MAP.put(userData, guessedList);
			post(userData.getName() + " added ticket: " + guessedList, channel);
		} catch(SQLException e) {
			e.printStackTrace();//TODO: log
		}

	}

	/**
	 * posts last winning numbers
	 */
	private static void last() {
		/*if (LAST_WINS.isEmpty()) {
			post("es gab noch keine Ziehung!");
			return;
		}
		post("Letzte Ziehung: " + LAST_WINS);*/
	}

	/**
	 * gets the date of next drawing
	 *
	 * @return date of next drawing
	 */
	private static LocalDateTime getNextDrawing() {
		//return LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00
		return LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(rotateDay(false)))).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00
		/*LocalDateTime test = LocalDateTime.of(2016, 10, 27, 21, 10, 5);
		System.out.println(DayOfWeek.of(lastDay));
		return test.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(softRotate()))).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00*/
		//return LocalDateTime.now().withDayOfMonth(LocalDateTime.now().getDayOfMonth() + 5).withHour(20).withMinute(0).withSecond(0);//alle 5 tage 20:00:00
		//return LocalDateTime.now().withSecond(LocalDateTime.now().getSecond() + 10);//in 30 sekunden
	}

	/**
	 * rotates the day to the next drawing occurrence
	 *
	 * @param hardChange should the next day get changed permanently
	 * @return day of next drawing
	 */
	protected static int rotateDay(boolean hardChange) {
		int tmpDay = lastDay + 5;
		if (tmpDay > 7) tmpDay -= 7;
		if (hardChange) lastDay = tmpDay;
		return tmpDay;
	}
}
