package dbot.comm;

/**
 * Created by Niklas on 19.10.2016.
 */
public class Lotto {
	/*private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.LottoTimer");
	protected static int lastDay = Database.getInstance().getServerData().getLastLottoDay();//1 = Monday, 7 = Sunday
	protected static final int POT_MAX = 420000;
	private static final int PRICE = 250;
	protected static final DataMap<UserData, ArrayList<Integer>> TICKET_MAP = new DataMap<>();
	protected static int pot = Database.getInstance().getServerData().getLottoPot();
	protected static final ArrayList<Integer> LAST_WINS = new ArrayList<>();
	protected static boolean closed = false;

	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
	private static final LottoTimer LOTTO_TIMER = new LottoTimer();
	static {
		//TODO: berechnung von initdelay (mit serverdata)
		//LocalDateTime nextLotto = LocalDateTime.of(2016, Month.OCTOBER, 19, 15, 52);
		LocalDateTime nextLotto = getNextDrawing();

		System.out.println(nextLotto);
		long initDelay = Duration.between(LocalDateTime.now(), nextLotto).getSeconds();
		System.out.println(initDelay);
		SCHEDULER.scheduleAtFixedRate(LOTTO_TIMER, initDelay, 60 , SECONDS);//alle 5 Tage in sec: 60 * 60 * 24 * 5
	}

	static void addTicket(UserData userData, String params) {


		if (params.equals("last")) {//TODO: regex?
			last();
			return;
		}

		if (params.equals("pool")) {
			post("im Pool: " + pot);
			return;
		}

		if (params.equals("next")) {
			LocalDateTime next = getNextDrawing();
			post("Nächste Ziehung ist am " + next.format(DateTimeFormatter.ofPattern("dd.MM.")) + " um " + next.format(DateTimeFormatter.ofPattern("HH:mm")));
			return;
		}

		if (closed) {
			System.out.println("closed");
			post(userData.getName() + " nicht cheaten du Noob...");
			return;
		}

		if (TICKET_MAP.getKeysOfKey(userData).size() >= 3) {//3 Lose max
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
		post(userData.getName() + " added ticket: " + guessedList);
		TICKET_MAP.put(userData, guessedList);
	}

	private static Matcher match(String s) {
		return Pattern.compile("(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(1[0-6]|[1-9])\\s(!k|k)").matcher(s);
	}

	private static void last() {
		if (LAST_WINS.isEmpty()) {
			post("es gab noch keine Ziehung!");
			return;
		}
		post("Letzte Ziehung: " + LAST_WINS);
	}

	private static LocalDateTime getNextDrawing() {
		//return LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00
		return LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(rotateDay(false)))).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00
		/*LocalDateTime test = LocalDateTime.of(2016, 10, 27, 21, 10, 5);
		System.out.println(DayOfWeek.of(lastDay));
		return test.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(softRotate()))).withHour(20).withMinute(0).withSecond(0);//jeden donnerstag 20:00:00*/
		//return LocalDateTime.now().withDayOfMonth(LocalDateTime.now().getDayOfMonth() + 5).withHour(20).withMinute(0).withSecond(0);//alle 5 tage 20:00:00
		//return LocalDateTime.now().withSecond(LocalDateTime.now().getSecond() + 15);//in 15 sekunden
	/*}

	protected static int rotateDay(boolean hardChange) {
		int tmpDay = lastDay + 5;
		if (tmpDay > 7) tmpDay -= 7;
		if (hardChange) lastDay = tmpDay;
		return tmpDay;
	}

	public static int getPot() {
		return pot;
	}

	public static int getLastDay() {
		return lastDay;
	}*/
}
