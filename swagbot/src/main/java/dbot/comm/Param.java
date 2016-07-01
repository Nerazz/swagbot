package dbot.comm;

public class Param/*<String>*/ {//was macht protected und was public sinn?
	private Object value = null;
	//E benutzen und irgendwie typecheck als methode
	protected Param(String content) {
		try {
			double d = Double.parseDouble(content);
			value = d;
			if ((d % 1.0) == 0.0) {
				//int i = (int)d;
				value = (int)d;
			}
		} catch(NumberFormatException e) {
			value = content;
		}
	}
	public Object getValue() {//hier vielleicht getInt, getDouble, ... mit Fehlerdings machen?
		return value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
