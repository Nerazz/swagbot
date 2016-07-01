package dbot.comm;

import mib.*;
import dbot.timer.CDDel;
import dbot.Poster;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import sx.blah.discord.api.IDiscordClient;

import java.util.List;
import java.util.*;

public final class Roll {
	public static void m(Poster pos, IUser author, List<Param> paramList) {
		//for
		//if ((paramList.get(1) == null) && ())//vorher fehler abfangen oder einfach mit exception?(param1 instanceof Integer) && (
		//try {
		if ((paramList.get(1).getValue() instanceof Integer) {//indexoutofboundsexception...
			int param1 = (int)paramList.get(1).getValue();
			if (param1 >= 0) {
				if (paramList.get(2).getValue() instanceof Integer) {
					int param2 = (int)paramList.get(2).getValue();
					if ((param2 >= 0) && (param1 <= param2)) {
						int rnd = (int)(Math.random() * (param2 - param1 + 1)) + param1;
						pos.post(":game_die: " + author + " hat eine " + rnd + " aus " + param1 + " - " + param2 + " gewürfelt! :game_die:");
					}
				}
				else {
					int rnd = (int)(Math.random() * param1) + 1;
					pos.post(":game_die: " + author + " hat eine " + rnd + " aus " + param1 + " gewürfelt! :game_die:");
				}
			}
		}
		else {
			int rnd = (int)(Math.random() * 100) + 1;
			if (rnd != 100) {
				pos.post(":game_die: " + author + " hat eine " + rnd + " gewürfelt! :game_die:");
			}
			else {
				pos.post(":slot_machine: " + author + " hat eine :100: gewürfelt!!! :slot_machine:\ngz :ok_hand:");
			}
		}
		
		System.out.println("durch");
	}
}
