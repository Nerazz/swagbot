package dbot.timer;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IMessage;

import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.DiscordException;



public class CDDel implements Runnable {
	
	private IMessage m = null;
	private IMessage n = null;
	private int len = 60000; //vielleicht auf 5/10 min stellen?
	
	public CDDel(IMessage m, IMessage n) {
		this.m = m;
		this.n = n;
		Thread tCDDel = new Thread(this, "TimerDel Thread");
		//System.out.println("created: " + tCDDel);
		
		tCDDel.start();
	}
	
	public CDDel(IMessage m) {
		this.m = m;
		Thread tCDDel = new Thread(this, "TimerDel Thread");
		//System.out.println("created: " + tCDDel);
		
		tCDDel.start();
	}
	
	public CDDel(IMessage m, int len) {
		this.len = len;
		this.m = m;
		Thread tCDDel = new Thread(this, "TimerDel Thread");
		//System.out.println("created: " + tCDDel);
		
		tCDDel.start();
	}

	public void run() {
		try {
			if (len != 0) {
				Thread.sleep(len);
			}
			if (m != null) {
				m.delete();
			}
			if (n != null) {
				n.delete();
			}
			
		} catch(MissingPermissionsException e) {
			e.printStackTrace();
		} catch(HTTP429Exception e) {
			e.printStackTrace();
		} catch(DiscordException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
