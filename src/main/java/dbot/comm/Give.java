package dbot.comm;

import dbot.UserData;

public final class Give {
	
	public static void m(UserData dGiver, String sGems, UserData dGetter) {
		try {
			int gems = Integer.parseInt(sGems);
			if ((dGiver.getGems() < gems) || (gems < 1)) {
				System.out.println("fail");
				return;
			}
			dGiver.subGems(gems);
			dGetter.addGems(gems);
			System.out.println("gave " + gems + " gems.");
		} catch(Exception e) {
			System.out.println("parseerror Give.m");
		}
		
	}
	
}
