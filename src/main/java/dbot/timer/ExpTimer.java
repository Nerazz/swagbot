package dbot.timer;

import dbot.UserData;

public class ExpTimer implements Runnable {
	
	private UserData uData;
	private double amp;
	private int time;
	
	public ExpTimer(UserData uData, double amp, int time) {
		this.uData = uData;
		this.amp = amp;
		this.time = time;
		Thread tExpTimer = new Thread(this, "ExpTimer Thread");
		System.out.println("created:" + tExpTimer);
		tExpTimer.start();
	}
	
	public void run() {
		uData.setExpRate(amp);
		try {
			Thread.sleep(time);
		} catch(Exception e) {
			System.out.println(e);
		} finally {
			uData.setExpRate(1.0);
			System.out.println("exp durch");
		}
		
	}
	
}
