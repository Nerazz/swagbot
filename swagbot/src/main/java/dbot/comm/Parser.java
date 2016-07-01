package dbot.comm;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import java.util.List;
import java.util.*;

import dbot.timer.CDDel;
import dbot.Poster;

public class Parser {
	protected static Poster pos = new Poster();//wird jedes mal gecallt? testen mit sysprint in posterconstructor
	private IMessage message;
	private String content;
	private List<Param> paramList = new ArrayList<Param>();//muss linkedlist sein (fuer richtige reihenfolge von params)??? eigentlich nicht
	//private IChannel channel;
	private IUser author;
	private static final String idNeraz = "97092184821465088";
	
	public Parser(IMessage message) {
		this.message = message;
		content = message.getContent().toLowerCase();
		author = message.getAuthor();
		parse();
		//channel = message.getChannel();
	}
	
	public void parse() {
		
		//paramList.add(new Param(content));//bei commands mit paramList.size() parameterzahl pruefen
		
		if (content.startsWith("!") && (content.length() < 50)) {//! dranlassen zur admincommand unterscheidung?
			System.out.println("parsertrigger");
			content = content.substring(1);
			String test = null;
			int i = 0;
			int iWSpace = 0;
			int paramLen = 0;
			int contentLen = content.length();
			int params = contentLen - content.replace(" ", "").length();
			if (params < 6) {
				while ((contentLen > 0)) {
					if ((iWSpace = content.indexOf(' ')) != -1) {
						test = content.substring(0, iWSpace);
						paramList.add(new Param(test));
						content = content.substring(iWSpace + 1);
						contentLen -= (content.length() + 1);
						i++;
					}
					else {
						paramList.add(new Param(content));
						//System.out.println(param[i]);
						contentLen = 0;
					}
				}
			}
			System.out.println(paramList);
			new CDDel(message);
			Commands.user(this);//muss Commands. davor?
		}
		
		
		else if (content.startsWith("§")) {
			
			Commands.admin(this);//String returnen und hier posten?
			
		}
	}
	
	public IMessage getMessage() {
		return message;
	}
	
	protected IUser getAuthor() {
		return author;
	}
	protected List<Param> getParams() {
		return paramList;
	}
}
