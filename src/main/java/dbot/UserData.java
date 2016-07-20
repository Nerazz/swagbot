package dbot;
import sx.blah.discord.handle.obj.IUser;

public class UserData extends DataBase {//implements comparable?
	private String id = null;
	private IUser user = null;
	private String name = null;
	private int gems = -1;
	
	private String rpgClass = null;
	private int rpgExp = -1;
	private int rpgLevel = -1;
	private int rpgPresLevel = -1;
	private double rpgExpRate = 1;
	
	private int rpgPotDuration = 0;
	
	protected UserData(IUser user) {
		this.user = user;
		id = user.getID();
		name = user.getName();
		gems = 0;
		rpgClass = "peasant";
		rpgExp = 0;
		rpgLevel = 1;
		rpgPresLevel = 0;
	}
	
	public String getID() {
		return id;
	}
	
	public IUser getUser() {
		return user;
	}
	
	public String getName() {
		return name;
	}
	
	public int getGems() {
		return gems;
	}
	
	public void setGems(int gems) {
		this.gems = gems;
	}
	
	public void addGems(int gems) {
		this.gems += gems;
	}
	
	public void subGems(int gems) {
		this.gems -= gems;
	}
	
	public int getExp() {
		return rpgExp;
	}
	
	protected void setExp(int rpgExp) {
		this.rpgExp = rpgExp;
	}
	
	public void addExp(int rpgExp) {
		this.rpgExp += rpgExp;
		if(this.rpgLevel == 100) {
			
		}
		else if (this.rpgExp >= rpgLevelThreshold[getLevel() - 1]) {
			this.rpgLevel += 1;//setter benutzen?
			this.rpgExp -= rpgLevelThreshold[getLevel() - 2];
			//new Poster pos;
			new Poster().post(":tada: DING! " + user + " ist Level " + this.getLevel() + "! :tada:");
		}
	}
	
	public int getLevel() {
		return rpgLevel;
	}
	
	protected void setLevel(int rpgLevel) {
		this.rpgLevel = rpgLevel;
	}
	
	public void resetLevel() {
		rpgLevel = 1;
	}
	
	public String getrpgClass() {
		return rpgClass;
	}
	
	public void setrpgClass(String rpgClass) {
		System.out.println("\"" + rpgClass + "\"");
		if ((rpgClass.equals("krieger")) || (rpgClass.equals("mage")) || (rpgClass.equals("hunter"))) {
			this.rpgClass = rpgClass;
			System.out.println("new class: " + this.rpgClass);
			return;
		}
		System.out.println("klasse nicht geupdated");
	}
	
	public int getPresLevel() {
		return rpgPresLevel;
	}
	
	protected void setPresLevel(int rpgPresLevel) {
		this.rpgPresLevel = rpgPresLevel;
	}
	
	public void addPresLevel() {
		rpgPresLevel += 1;
	}
	
	public double getExpRate() {
		return rpgExpRate;
	}
	
	public void setExpRate(double rpgExpRate) {
		this.rpgExpRate = rpgExpRate;
	}
	
	public int getPotDuration() {
		return rpgPotDuration;
	}
	
	public void setPotDuration(int rpgPotDuration) {
		if (rpgPotDuration < 0) {
			System.out.println("ERROR: PotDuration darf nicht < 0 sein!");
			return;
		}
		this.rpgPotDuration = rpgPotDuration;
	}
	
	public void reducePotDuration() {
		if (rpgPotDuration > 0) {
			rpgPotDuration -= 1;
			if (rpgPotDuration < 1) {
				setExpRate(1);
				System.out.println("xpot von " + name + " durch");
			}
		}
	}
	
	/*@Override
	public boolean equals(Object o) {
		Data data = new Data((IUser)o);
		data = (Data)o;
		if (data.getID() == this.getID()) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}*/
	
}
