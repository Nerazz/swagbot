package dbot.timer;

/**
 * Created by niklas on 29.09.16.
 */
final class RaffleTimer implements Runnable {

	@Override
	public void run() {
		System.out.println("RIP");
	}
	/*private static final Logger LOGGER = LoggerFactory.getLogger("dbot.timer.RaffleTimer");
	private List<UserData> dataList = new LinkedList<>();
	private List<Integer> betList = new LinkedList<>();
	private int pot = 0;
	//private static boolean isRunning = false;
	private boolean closed = false;
	private static RaffleTimer instance = null;

	private RaffleTimer() {}

	static void m() {

	}

	public static void m(UserData userData, String params) {
		Matcher matcher = Pattern.compile("\\d+").matcher(params);//TODO: alle regexes so machen
		if (!matcher.matches()) return;
		int bet = Integer.parseInt(matcher.group());
		if (bet > userData.getGems()) {
			post(userData.getName() + ", du hast zu wenig :gem:.");
			return;
		}

		if (instance == null) {
			instance = new RaffleTimer();
			System.out.println("vor");
			new Thread(instance, "test").start();
			//instance.run();
			System.out.println("nach");
			LOGGER.info("created new RaffleTimer");
		}
		if (instance.closed) return;
		System.out.println("1");
		if (instance.dataList.contains(userData)) {
			post(userData.getName() + ", du bist schon eingetragen!");
			return;
		}
		System.out.println("2");
		userData.subGems(bet);
		instance.pot += bet;
		instance.dataList.add(userData);
		instance.betList.add(bet);
		post("added " + userData.getName());
		LOGGER.debug("added {} with {}", userData.getName(), bet);
		System.out.println("1.1");

		/*if (!isRunning) {
			System.out.println("2");
			instance.run();
			//new Timer().schedule(instance, 5000, 0);
			System.out.println("3");
		}*/
	/*}

	@Override
	public void run() {
		System.out.println("started timer");
		//isRunning = true;
		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			LOGGER.error("interrupted run", e);
		}
		closed = true;
		System.out.println("closed timer");
		//UserData winner =
		int rng = (int)Math.round(Math.random() * (pot - 1) + 1);//0 muss übersprungen werden!, tuts noch nicht
		System.out.println("rng: " + rng);
		int x = 0, i = 0;
		while (rng < x && i < betList.size()) {
			x += betList.get(i);
			System.out.println("i: " + i + "x: " + x);
			i++;
		}
		UserData winner = dataList.get(i);
		post(winner.getName() + " hat mit " + betList.get(i) + " gewonnen!");
		LOGGER.info("{} hat mit {} gewonnen.", winner.getName(), rng);

		instance = null;
	}*/
}
