package dbot;

public class ServerData extends DataBase {
	private static int gems;
	
	protected ServerData() {
		
	}
	
	public void addGems(int aGems) {
		gems += aGems;
	}
	
	public boolean subGems(int sGems) {
		if ((gems - sGems) >= 0) {
			this.gems -= gems;
			return true;
		}
		else {
			System.out.println("ACHTUNG: BANK LEER");
		}
		return false;
		
	}
	
	public int getGems() {
		return gems;
	}
	
}
