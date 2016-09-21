package dbot.timer;

import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class DelTimer implements Runnable {
	
	private IMessage message = null;
	private int duration = 60000;
	
	public DelTimer(IMessage message, int duration) {
		this.message = message;
		this.duration = duration;
		Thread tDelTimer = new Thread(this, "DelTimer Thread");
		tDelTimer.start();
	}
	
	public void run() {
		try {
			if (message != null) {
				Thread.sleep(duration);
				message.delete();
			} else {
				System.out.println("DelTimer.Message == null");
			}
		} catch(MissingPermissionsException e) {
			System.out.println("MissingEX: DelTimer.run");
		} catch(RateLimitException e) {
			System.out.println("RateLimitEX: DelTimer.run");
		} catch(DiscordException e) {
			System.out.println("DiscordEX: DelTimer.run");
		} catch(InterruptedException e) {
			System.out.println("InterruptedEX: DelTimer.run");
		}
	}
}
