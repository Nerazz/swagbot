package dbot.timer;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;

public class DelTimer implements Runnable {
	
	private IMessage message = null;
	private int duration = 60000;
	
	public DelTimer(IMessage message) {
		this.message = message;
		Thread tDelTimer = new Thread(this, "DelTimer Thread");
		tDelTimer.start();
	}
	
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
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("MissingEX: DelTimer.run");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(HTTP429Exception e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("HTTPEX: DelTimer.run");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(DiscordException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("DiscordEX: DelTimer.run");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		} catch(InterruptedException e) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
			System.out.println("InterruptedEX: DelTimer.run");
			System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		}
	}
}
